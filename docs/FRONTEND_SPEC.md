# Frontend Specification (Android, sample data)

Goal: a complete, clickable version of the ROAVAI Parent App that runs on **sample data**, so the screens and flow can be reviewed before any backend exists. Content comes from `PRD.md`. Data comes from `SAMPLE_DATA.md`.

---

## 1. Architecture

```
UI (Compose screen)  ->  ViewModel (UiState)  ->  Repository interface  ->  Fake repository (sample data)
```

Later, the fake repositories are replaced by real ones (Retrofit + Room). The UI and ViewModels must **not** change when that happens. That is why every data source is behind an interface.

### Package structure `com.roavai.parent`
```
core/
  di/              Hilt modules (bind interfaces to fakes)
  util/            UiState, Result, date formatting helpers
domain/
  model/           data classes and enums (Section 2)
  repository/      repository interfaces (Section 3)
data/
  fake/            fake repositories, SampleData object, DemoSettings
ui/
  theme/           colors, typography, shapes
  components/      shared composables (Section 6)
  navigation/      routes, NavHost, bottom bar
  splash/ setup/ onboarding/ auth/ home/ progress/ activity/ goals/ robot/ profile/ notifications/
```

### UiState (one per screen)
```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
    data class Offline(val cached: Any? = null) : UiState<Nothing>
}
```
Repositories return `AppResult<T>`: `Success(data)`, `Failure(message)`, `NoConnection`. ViewModels map them to `UiState`.

### Demo settings (for the review with sir)
`DemoSettings` is an in-memory singleton with a forced state: `NORMAL`, `LOADING`, `EMPTY`, `ERROR`, `OFFLINE`. Fake repositories read it:
- `NORMAL`: return sample data after a 600 ms delay
- `LOADING`: never finish loading
- `EMPTY`: return empty lists / no data
- `ERROR`: return `Failure("Something went wrong")`
- `OFFLINE`: return `NoConnection`

Open it from **anywhere** by long-pressing the screen title in the top bar, and also from **Profile > Demo controls**. Both are visible only in debug builds. The controls open as a bottom sheet with the five states and a "Robot disconnected" switch. It lets sir see every state without special data. Retry buttons re-run the call.

---

## 2. Domain models (`domain/model`)

```kotlin
data class Parent(val id: String, val fullName: String, val email: String, val mobile: String?)
data class Child(val id: String, val name: String, val grade: String, val curriculum: String,
                 val avatarUrl: String?, val dailyGoalMinutes: Int)

enum class WifiSignal { STRONG, MEDIUM, WEAK }
data class RobotStatus(val connected: Boolean, val wifiConnected: Boolean, val wifiSignal: WifiSignal,
                       val batteryPercent: Int, val lastActive: Instant?, val firmwareVersion: String?)

enum class ActivityType { PRACTICE, STUDY, QUIZ, LESSON, READING, EDUCATIONAL_ACTIVITY }
enum class ActivityStatus { COMPLETED, IN_PROGRESS }
data class Activity(val id: String, val title: String, val subject: String, val topic: String,
                    val summary: String, val type: ActivityType, val status: ActivityStatus,
                    val startedAt: Instant, val durationMinutes: Int)

data class TodaySummary(val learningMinutes: Int, val dailyGoalMinutes: Int, val topicsCovered: Int,
                        val subjects: List<String>, val questionsAnswered: Int,
                        val correct: Int, val incorrect: Int, val overallProgressPercent: Int)
data class SubjectProgress(val subject: String, val percent: Int, val completedTopics: Int,
                           val totalTopics: Int, val needsPracticeTopics: Int)
data class Goal(val id: String, val title: String, val subject: String, val topic: String?,
                val date: LocalDate, val time: LocalTime?, val done: Boolean)
enum class AchievementKind { STREAK, CONSISTENT_LEARNING, SUBJECT_ACHIEVEMENT, MILESTONE }
data class Achievement(val id: String, val kind: AchievementKind, val title: String,
                       val description: String, val earnedAt: Instant)

data class HomeData(val child: Child, val robot: RobotStatus?, val today: TodaySummary,
                    val recentActivity: List<Activity>, val subjectProgress: List<SubjectProgress>,
                    val upcomingGoals: List<Goal>, val achievements: List<Achievement>,
                    val unreadNotifications: Int)

enum class Period { THIS_WEEK, LAST_WEEK, THIS_MONTH, LAST_MONTH }
enum class ActivityPeriod { TODAY, THIS_WEEK, THIS_MONTH, ALL_TIME }

data class DayValue(val date: LocalDate, val value: Int)
data class OverallProgress(val percent: Int, val statusLabel: String, val completedActivities: Int,
                           val inProgressActivities: Int, val perDay: List<DayValue>)
data class LearningTime(val totalMinutes: Int, val dailyGoalMinutes: Int, val perDay: List<DayValue>)
enum class TopicStatus { COMPLETED, LEARNING, NEEDS_PRACTICE }
data class TopicMastery(val subject: String, val topic: String, val percent: Int, val status: TopicStatus)
data class TestResult(val id: String, val name: String, val subject: String, val date: LocalDate,
                      val score: Int, val total: Int, val correct: Int, val incorrect: Int, val percentage: Int)
data class ComparisonRow(val label: String, val current: String, val previous: String, val change: String)
data class Comparisons(val learningTime: ComparisonRow, val overallProgress: ComparisonRow,
                       val testAverage: ComparisonRow, val subjects: List<ComparisonRow>)
data class CreditEntry(val id: String, val amount: Int, val reason: String, val activityTitle: String?,
                       val achievementTitle: String?, val earnedAt: Instant)
data class CreditSummary(val total: Int, val recent: List<CreditEntry>, val motivation: String)

enum class NotificationType { LEARNING_UPDATE, ROBOT_UPDATE, IMPORTANT, ACHIEVEMENT }
data class AppNotification(val id: String, val title: String, val message: String,
                           val type: NotificationType, val read: Boolean, val createdAt: Instant)
```

---

## 3. Repository interfaces (`domain/repository`)

All functions are `suspend` and return `AppResult<...>`.

| Interface | Functions |
|---|---|
| `AuthRepository` | `login(email, password)`, `signUp(fullName, email, mobile?, password)`, `requestOtp(email)`, `verifyOtp(email, otp)`, `resetPassword(email, newPassword)`, `currentParent()`, `isLoggedIn()`, `logout()` |
| `ChildRepository` | `addChild(name, grade, curriculum, consent)`, `currentChild()` |
| `RobotRepository` | `pair(code)`, `status()`, `unpair()` |
| `HomeRepository` | `home()` |
| `ActivityRepository` | `activities(period, subject?)`, `activity(id)` |
| `ProgressRepository` | `overall(period)`, `learningTime(period)`, `subjects(period)`, `subjectTopics(subject)`, `topics(subject?)`, `tests(period)`, `comparisons(period)`, `credits()`, `creditHistory()` |
| `GoalsRepository` | `goals()`, `addGoal(...)`, `updateGoal(...)`, `deleteGoal(id)`, `setDailyGoal(minutes)` |
| `NotificationsRepository` | `notifications()`, `markRead(id)`, `markAllRead()`, `unreadCount()` |

Goals, notification read state and the logged-in state are kept **in memory** so changes made in the app show up on other screens until the app restarts.

---

## 4. Navigation

### Routes
| Route | Screen |
|---|---|
| `splash` | Splash |
| `robot_setup` | Robot Setup |
| `onboarding/{page}` | Onboarding 1, 2, 3 |
| `signup` | Sign Up |
| `login` | Login |
| `forgot/email`, `forgot/otp`, `forgot/new_password` | Forgot password, 3 steps |
| `add_child` | Add Child with consent |
| `home` | Home |
| `progress` | Progress hub |
| `progress/overall`, `progress/learning_time`, `progress/subjects`, `progress/subjects/{subject}`, `progress/topics`, `progress/tests`, `progress/comparisons`, `progress/credits` | Progress details |
| `activity` | Activity list |
| `activity/{id}` | Activity details |
| `robot` | Robot tab |
| `profile` | Profile tab |
| `goals` | Goals list |
| `goals/edit?id={id}` | Add / edit goal |
| `notifications` | Notifications |
| `achievements` | All achievements (Home View All) |

### Flow
```
splash -> (not logged in) robot_setup -> onboarding/1 -> /2 -> /3 -> signup or login -> (new account) add_child -> home
splash -> (logged in) home
```
> **Note:** the PRD places Robot Setup before sign up. In the real app, pairing needs an account, so the real pairing call happens after login. For the demo, follow the PRD order. Confirm with sir.

### Bottom bar
Home | Progress | Activity | Robot | Profile. Shown on the five tab screens and on their detail screens. Hidden on splash, setup, onboarding, auth, add child. The selected tab is highlighted. Tab state is kept when switching tabs. Back from a detail screen returns to its tab.

---

## 5. Theme

Material 3, light and dark. Use these as **placeholders** until Figma designs arrive; keep them in one place so they are easy to replace.

| Token | Light | Dark |
|---|---|---|
| primary | `#0F6E56` | `#5DCAA5` |
| secondary | `#534AB7` | `#AFA9EC` |
| background | `#FFFFFF` | `#14181C` |
| surface card | `#F6F8F7` | `#1D242B` |
| error | `#B3261E` | `#F2B8B5` |
| success | `#2E7D32` | `#81C784` |

Typography: system default, Material 3 scale. Corner radius 12dp for cards, 8dp for chips. Spacing grid 4dp, screen padding 16dp.

---

## 6. Shared components (`ui/components`)

| Component | Purpose |
|---|---|
| `ScreenScaffold` | Top app bar (menu icon, title, description, bell with unread badge, profile avatar) + content + bottom bar |
| `ChildHeader` | Avatar, name, class/grade, curriculum/board |
| `SectionHeader` | Title with optional "View All" |
| `StatCard` | Label + big value + optional subtitle |
| `ProgressBar` | Labeled percentage bar |
| `RobotStatusCard` | Connected / disconnected, Wi-Fi, battery, last active, "View details" |
| `ActivityRow` | Title, subject, topic, what the child did, type chip, date/time, status |
| `GoalRow` | Activity name, subject/topic, date, time |
| `AchievementChip` | Icon + title |
| `PeriodSelector` | This Week / Last Week / This Month / Last Month |
| `SimpleBarChart` | Per-day bars drawn with Canvas (no chart library) |
| `EmptyState` | Icon, title, message |
| `ErrorState` | Message + Retry button |
| `OfflineBanner` | "Cannot update right now" |
| `LoadingSkeleton` | Placeholder shimmer for cards and rows |
| `StateHost` | Takes a `UiState` and shows loading / empty / error / offline / content |
| `PrimaryButton`, `TextField with error` | Forms |

`StateHost` is used by every data screen so the five states look the same everywhere.

---

## 7. Screen specifications

Texts marked "exact" must match the PRD.

### 7.1 Splash
- ROAVAI logo (placeholder vector), app name, tagline "Your child's learning companion" (placeholder tagline).
- Show 1.5 seconds, then navigate automatically. No interaction.
- Not logged in: `robot_setup`. Logged in: `home`.

### 7.2 Robot Setup (Wi-Fi)
- Title "Connect your ROAVAI robot". Instruction: enter the code shown on the robot.
- Code field (8 characters, uppercase), "Connect" button.
- States: idle, connecting (spinner), success ("Robot connected", Continue), error (message + Retry).
- Demo codes in `SAMPLE_DATA.md`.
- Link "Skip for now" is **not** shown (PRD has none).
- Note under the form: "Bluetooth and QR setup are coming soon" (small, informational).

### 7.3 Onboarding (3 pages, pager with dots)
| Page | Title | Content | Message (exact) | Buttons |
|---|---|---|---|---|
| 1 | Meet ROAVAI | Robot image, name, short intro, how ROAVAI helps the child | "Meet your child's AI learning companion." | Next / Skip |
| 2 | Learn, Explore & Grow | Interactive learning, Questions & answers, Educational activities, Different subjects/topics, Personalized learning | "Learning that feels like play." | Next / Skip |
| 3 | Stay Connected | Child's learning activity, Learning progress, Activity/session history, Achievements, Robot status | "See how your child learns and grows." | Get Started |
- Skip goes to `signup`. Get Started goes to `signup` (with "Already have an account? Log in").

### 7.4 Sign Up
- Fields: Full Name, Email Address, Password, Confirm Password, Mobile Number (optional).
- Validation on submit and on field blur: required fields, valid email, password at least 8 characters, passwords match.
- Errors under each field. Email `taken@example.com` returns "Email already registered".
- Success: message "Account created", then `add_child`.

### 7.5 Login
- Fields: Email Address, Password; "Forgot password?" link.
- Wrong credentials: "Invalid email or password".
- Success: `home` (or `add_child` if no child yet).

### 7.6 Forgot password (3 screens)
1. Enter registered email, "Send OTP".
2. Enter 6-digit OTP, "Verify". Wrong OTP error. "Resend OTP" with a 30 second countdown.
3. New password + Confirm password; must match. Success returns to Login with a snackbar "Password updated".

### 7.7 Add Child (added by us, needed for consent)
- Name, grade (dropdown), curriculum/board (dropdown), and four consent switches: voice, expression, store reasoning, use data to improve models. All four must be explicitly set by the parent (no defaults on). "Continue" enabled when name, grade and board are set.
- Short text explaining what each switch means.

### 7.8 Home (PRD module 5)
Scrollable, in this order:
1. **Header:** welcome message ("Welcome, Meera"), child's name, menu icon, bell with unread badge, parent avatar.
2. **Child info:** avatar, name, class/grade, curriculum/board, "View Profile" button, robot image, motivational message.
3. **Robot status:** connected or disconnected, Wi-Fi status, battery level, last active, "View details" opens `robot`.
4. **Today's learning:** total time today, daily goal (with progress bar), topics covered, subjects covered, questions answered, correct, incorrect, overall progress.
5. **Recent activity:** latest 3 activities, "View All" opens `activity`.
6. **Subject progress:** percentage per subject, "View All" opens `progress/subjects`.
7. **Upcoming goals:** activity name, subject/topic, date, time. "Add goal" button opens `goals/edit`.
8. **Achievements:** latest achievements, "View All" opens `achievements`.
9. **Notifications:** bell opens `notifications`.
10. Bottom bar.
11. Errors: clear message with Retry.

### 7.9 Progress hub (PRD 6.1 to 6.4, 6.9)
- Header: menu, title "Progress", description "Track child's learning progress", bell, avatar.
- Child info and the **period selector**.
- Cards: Overall Progress (percentage, status, period), Learning Time (total, period), Subject Progress, Topics Mastery, Test Performance, Comparisons, Credits (total, recent credits with reason). Each card opens its detail screen.
- A short motivational message with credits.

### 7.10 Progress detail screens (all have the period selector except where noted)
| Screen | Shows |
|---|---|
| Overall Progress | Overall percentage, status, completed activities, activities in progress, progress over the period (bar chart) |
| Learning Time | Total time, time per day (bar chart with daily goal line), daily goal |
| Subject Progress | List of subjects with percentage, completed topics, topics needing more practice; tap a subject for its topic list (no period selector) |
| Topics Mastery | Subject, topic, progress, grouped as Completed, Currently learning, Needs more practice; filter by subject |
| Test Performance | List: test/quiz name, subject, date, score, total questions, correct, incorrect, percentage |
| Comparisons | Current vs previous: learning time, overall progress, test performance, subject progress; shown as rows with the change (up/down arrow and value) |
| Credits | Total credits, list: credits earned, activity that earned them, date, achievement or reward |
Every detail screen has loading, empty ("No data available"), error with Retry, and offline.

### 7.11 Activity list (PRD module 7)
- Header: menu, title "Activity", description "Track your child's learning activities.", bell.
- Child info.
- Filters: time (Today, This Week, This Month, All Time) and subject (All Subjects, Mathematics, Science, Social Science, English) as chips.
- Timeline list grouped by day (Today, Yesterday, date). Each row: title, subject, topic, what the child did, type chip, date/time, status.
- Tap a row opens details.
- States: loading, list, empty ("No activities yet" / "Your child's learning activities will appear here."), error with Retry, offline ("Activity data cannot be updated right now").

### 7.12 Activity details
Title, subject, topic, what the child did, activity type, date/time, completion status. **Nothing else.** Do not add metrics such as accuracy or duration unless the PRD says so.

### 7.13 Goals
- List of upcoming goals with edit and delete (swipe or menu, with a delete confirmation).
- Daily learning goal setting (minutes, 5 to 480) at the top.
- Add / edit form: activity name, subject (dropdown), topic (optional), date (cannot be in the past), time (optional). Save adds it to the in-memory list; Home updates.

### 7.14 Notifications
List with title, message, time, unread dot; tap marks read; "Mark all as read" action. Empty state: "No notifications".

### 7.15 Robot tab
Status card (connected, Wi-Fi, battery, last active, firmware), "Reconnect" (opens Robot Setup), "Unpair" with confirmation. Disconnected state shows a warning colour and the last active time. If no robot is paired: empty state with "Connect robot".

### 7.16 Profile tab
- Parent profile (name, email, mobile) with Edit.
- Child profile (name, grade, board, daily goal) with Edit.
- Notification preferences placeholder switches (visual only).
- Privacy: consent switches, "Export my child's data" and "Delete child data" (confirmation dialogs, fake result).
- Log out.
- **Demo controls** (debug only): state selector NORMAL, LOADING, EMPTY, ERROR, OFFLINE, and a "Robot disconnected" switch. The same sheet opens by long-pressing any screen title.

---

## 8. Text and localisation
- All user-visible text in `res/values/strings.xml` with clear keys (`home_welcome`, `activity_empty_title`).
- Dates and times are formatted with the device locale and the device time zone. "Today", "Yesterday" computed from the current date.
- The fake repositories build sample data relative to **now**, so "Today" always has data.

## 9. Accessibility
- Content descriptions for icons, images, charts (chart summary text such as "Learning time this week, 4 hours 40 minutes").
- Touch targets at least 48dp.
- Works with system font scale up to 130%.
- Colour is never the only signal (disconnected has text, status chips have labels).
- Check light and dark mode.

## 10. Definition of done (frontend phase)
- Every route in Section 4 opens without a crash.
- Every data screen shows all five states using Demo controls.
- Numbers on screen match `SAMPLE_DATA.md` exactly.
- `.\gradlew.bat assembleDebug` and `.\gradlew.bat testDebugUnitTest` pass.
- No networking dependencies in `build.gradle`.
- No hardcoded text in Compose code.
