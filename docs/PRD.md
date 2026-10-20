# ROAVAI Parent App: Product Requirements (v1.2)

**Platform:** Android. **Users:** parents of children who learn with the ROAVAI robot.

---

## 1. The Idea in Short

The ROAVAI robot teaches the child. The Parent App lets the parent:

1. **Connect** the robot to the app over Wi-Fi. Bluetooth and QR code are planned for later.
2. **Sign up and log in.**
3. **See how the child is learning:** today's learning, progress by subject and topic, tests, activity history, achievements, credits.
4. **Set daily goals** for the child. The robot does not set them by itself.
5. **See robot status:** connected or not, Wi-Fi, battery, last active.

The robot collects the learning data and the app shows it. Daily goals are set in the app and sent to the robot.

## 2. Screen Map

```
App launch
  └─ Splash
       ├─ New user ─► Robot Setup (Wi-Fi) ─► Onboarding 1 ─► 2 ─► 3 ─► Get Started ─► Sign Up / Login ─► Home
       └─ Logged-in user ──────────────────────────────────────────────────────────────────────► Home

Bottom navigation:  Home | Progress | Activity | Robot | Profile

Progress ─► Overall Progress ─► Overall Progress Details
         ─► Learning Time ─► Learning Time Details
         ─► Subject Progress ─► Subject Details
         ─► Topics Mastery ─► Topics Details
         ─► Test Performance ─► Test Details
         ─► Comparisons ─► Comparison Details
         ─► Credits ─► Credits Details

Activity ─► Activity Details
Home ─► Upcoming Goals ─► Add / Edit Goal
Login ─► Forgot Password ─► OTP ─► New Password ─► Login
```

Module numbers in the document: 1 Splash, 2 Onboarding, 3 Sign Up / Login, 5 Home, 6 Progress, 7 Activity. There is no Module 4.

---

## 3. Module 1: Splash Screen

**Purpose:** introduce the ROAVAI Parent App while it initialises.

- Shown when the app is launched.
- Shows the ROAVAI logo and app name. A short tagline may be shown.
- App does its initialisation in the background while the splash is visible.
- After initialisation it navigates automatically:
  - New user goes to onboarding/authentication flow.
  - Existing authenticated user goes to the Parent Home Dashboard.
- Needs no user interaction.

## 4. Setup: Robot Pairing

- Parent can connect the ROAVAI robot to the app.
- App detects the available robot.
- Successful connection is confirmed.
- Error message is shown if connection fails.
- Parent can retry the connection.

**Connection methods in the document:** Bluetooth, Wi-Fi, QR code, Bluetooth + Wi-Fi (pair through Bluetooth, then configure Wi-Fi).

**Current robot:** setup is mainly over **Wi-Fi**. Bluetooth setup and QR display are not supported yet and are future work. Version 1 of the app supports Wi-Fi setup only.

How it works technically: Section 10.

## 5. Module 2: Onboarding

Flow: Robot Setup → Onboarding 1 → Onboarding 2 → Onboarding 3 → Get Started.

### Onboarding 1: Meet ROAVAI
- **Purpose:** introduce ROAVAI to the parent.
- **Content:** ROAVAI robot image, ROAVAI name, short introduction, how ROAVAI helps the child.
- **Message:** "Meet your child's AI learning companion."
- **Buttons:** Next / Skip.

### Onboarding 2: Learn, Explore & Grow
- **Purpose:** explain how the child learns with ROAVAI.
- **Content:** interactive learning, questions and answers, educational activities, different subjects/topics, personalised learning.
- **Message:** "Learning that feels like play."
- **Buttons:** Next / Skip.

### Onboarding 3: Stay Connected
- **Purpose:** explain what parents can see in the Parent App.
- **Content:** child's learning activity, learning progress, activity/session history, achievements, robot status.
- **Message:** "See how your child learns and grows."
- **Button:** Get Started.

## 6. Module 3: Sign Up / Login

### 6.1 Sign Up
**Purpose:** let new parents create a ROAVAI account.

**Fields:** Full Name, Email Address, Password, Confirm Password, Mobile Number (if required).

**Requirements:**
- Parent can create an account.
- Required fields are validated.
- Email must be valid and unique.
- Password and Confirm Password must match.
- Appropriate error messages are shown.
- A success message is shown after account creation.
- After successful registration, continue to the next setup step.

### 6.2 Login
**Purpose:** let registered parents access the app.

**Fields:** Email Address, Password.

**Requirements:**
- Parent can log in with registered credentials.
- Invalid credentials show an error message.
- Successful login goes to the Home Dashboard.
- Login includes a Forgot Password option.

### 6.3 Forgot / Reset Password
1. Parent enters registered email.
2. OTP is sent to the email.
3. Parent enters and verifies the OTP.
4. Parent creates a new password.
5. New password and confirmation must match.
6. After successful reset, return to Login.

---

## 7. Module 5: Parent Home Dashboard

**Purpose:** a simple view of how the child is learning and how the robot is doing.

**Bottom navigation:** Home | Progress | Activity | Robot | Profile. Home is highlighted on this page.

### 7.1 Header
- Welcome message for the parent
- Child's name
- Menu icon
- Notification icon
- Parent profile picture

### 7.2 Child Information
- Child's profile picture, name
- Class/Grade
- Curriculum/Board
- View Profile button
- ROAVAI robot image
- Short motivational message

### 7.3 Robot Status
- Robot connected or disconnected
- Wi-Fi status
- Battery level
- When the robot was last active
- Button to view robot details

### 7.4 Today's Learning
- Total learning time today
- Daily learning goal (set by the parent, see 7.7)
- Number of topics covered
- Subjects covered
- Number of practice questions answered
- Number of correct answers
- Number of incorrect answers
- Overall learning progress

### 7.5 Recent Activity
Each activity shows: subject, topic or activity name, what the child did, date and time, whether it was completed. A **View All** option opens the complete activity history.

### 7.6 Subject Progress
Progress percentage for each subject (for example Mathematics, Science, Social Science, English). A **View All** option shows more detailed progress.

### 7.7 Upcoming Goals
Upcoming learning activities or goals. Each shows: activity name, subject/topic, date, time.

**Set by the parent.** The robot does not assign daily tasks or goals by itself. The parent adds them in the app:
- Add, edit, and delete a goal (activity name, subject/topic, date, time).
- Set the daily learning goal.
- Goals are saved on the backend and the robot picks them up over Wi-Fi.

### 7.8 Achievements
Recent achievements, for example learning streak, consistent learning, subject achievement, other learning milestones. A **View All** option shows all achievements.

### 7.9 Notifications
- Parent can see new notifications.
- The notification icon shows the number of unread notifications.
- Notifications may include learning updates, robot updates, and important messages.

### 7.10 Errors
If information cannot be loaded, show a clear message and let the parent try again.

---

## 8. Module 6: Learning Progress

**Purpose:** let the parent easily see the child's learning progress, learning time, subject performance, test results, achievements, and earned credits.

### 8.1 Header
- Menu icon
- Page title: **Progress**
- Short description: "Track child's learning progress."
- Notification icon and unread notification count, if available
- Parent profile picture

### 8.2 Child Information and Time Period
- Child's profile picture, name, class/grade, curriculum/board
- Selected time period, which the parent can change: **This Week, Last Week, This Month, Last Month**

### 8.3 Overall Progress
- **Page shows:** overall progress percentage, progress status, selected time period.
- **Detail screen shows:** overall progress, completed activities, activities in progress, progress over the selected period.

### 8.4 Learning Time
- **Page shows:** total learning time, selected time period.
- **Detail screen shows:** total learning time, learning time for each day, daily learning goal (if available), learning time over the selected period.

### 8.5 Subject Progress
- **Detail screen shows:** list of subjects, progress percentage for each subject, completed topics, topics that need more practice.
- Parent can select a subject to see its detailed progress.

### 8.6 Topics Mastery
- **Detail screen shows:** subject, topic name, progress, topics completed, topics currently being learned, topics requiring more practice.

### 8.7 Test Performance
- **Detail screen shows:** test/quiz name, subject, date, score, total questions, correct answers, incorrect answers, percentage.

### 8.8 Comparisons
Parent can compare:
- Current progress with previous progress
- Learning time with previous periods
- Subject progress over time
- Test performance over time

### 8.9 Credits
- **Page shows:** total credits, recently earned credits, reason for earning credits, related achievement/reward.
- **Examples in the design:** Great Effort, Topic Master, Consistent Learner.
- **Detail screen shows:** total credits, credits earned, activity that earned the credits, date earned, achievements/rewards.

### 8.10 Motivation
The app may show a short positive message with the child's credits or achievements to encourage continued learning.

### 8.11 Bottom Navigation
Home | Progress | Activity | Robot | Profile. Progress is highlighted on this page.

### 8.12 Page Navigation
Progress → Overall Progress → Overall Progress Details
Progress → Learning Time → Learning Time Details
Progress → Subject Progress → Subject Details
Progress → Topics Mastery → Topics Details
Progress → Test Performance → Test Details
Progress → Comparisons → Comparison Details
Progress → Credits → Credits Details

### 8.13 Basic States
Loading, no data available, error while loading data, no internet connection.

---

## 9. Module 7: Activity Page

**Purpose:** let the parent view the child's learning activity history.

### 9.1 Header
- Menu icon
- Page title: **Activity**
- Short description: "Track your child's learning activities."
- Notification icon

### 9.2 Child Information
Selected child's profile picture, name, class/grade, curriculum/board. Example: Aarav D'Souza, Class 10 • CBSE.

### 9.3 Filters
- **Time period:** Today, This Week, This Month, All Time
- **Subject:** All Subjects, Mathematics, Science, Social Science, English

### 9.4 Activity List / Timeline
Each activity shows: activity title, subject, topic, what the child did, activity type, date/time, completion status.

Examples from the document:

| Title | What the child did | Type | When | Status |
|---|---|---|---|---|
| Mathematics: Quadratic Equations | Completed 15 questions | Practice | Today, 9:15 AM | Completed |
| Chemistry: Acids, Bases & Salts | Watched video + Notes | Study | Today, 8:30 AM | Completed |
| Physics: Light: Reflection & Refraction | Answered 10 questions | Practice | Yesterday, 7:45 PM | Completed |

### 9.5 Activity Details
Parent can select an activity to see its details: activity title, subject, topic, what the child did, activity type, date/time, completion status.

**No additional learning metrics shall be added unless confirmed as part of the ROAVAI system.**

### 9.6 Activity Types
Practice, Study, Quiz, Lesson, Reading, Educational Activity. Only activity types supported by ROAVAI are used.

### 9.7 Navigation
Home | Progress | Activity | Robot | Profile. Activity is highlighted on this page.

### 9.8 States
- **Loading:** activity data is being loaded.
- **Activities available:** activities are displayed.
- **Empty:** "No activities yet. Your child's learning activities will appear here."
- **Error:** error message with a Retry option.
- **Offline:** tell the parent activity data cannot currently be updated.

### 9.9 Acceptance Criteria
- Parent can open the Activity page.
- Parent can view the child's learning activities.
- Each activity displays the available activity information.
- Parent can filter by time period.
- Parent can filter by subject.
- Parent can open an activity to view its details.
- Completion status is displayed when available.
- Only the selected/authorised child's activities are displayed.
- Loading, empty, error (with Retry), and offline states are handled.
- Activity page follows the existing ROAVAI Parent App navigation.

---

## 10. How the App Connects to the Robot

The document lists four connection methods. The robot currently supports **Wi-Fi only**; the others are future work.

| Method | Status |
|---|---|
| **Wi-Fi** | Version 1 (now) |
| Bluetooth | Future |
| QR code | Future |
| Bluetooth + Wi-Fi | Future |

**Key idea:** the phone and robot are linked once during setup. After that the robot sends data to the cloud backend over Wi-Fi and the app reads it from there. Goals travel the other way: parent app → backend → robot.

```
Setup:   Parent app ──► Backend ◄── Robot   (robot linked to the child)
Data:    Robot ──Wi-Fi──► Backend ──► Parent app
Goals:   Parent app ──► Backend ──Wi-Fi──► Robot
```

**Wi-Fi setup (proposed):**
- **Robot already on Wi-Fi:** the robot registers with the backend and shows a pairing code. The parent enters the code in the app. The backend links the robot to the child. The app confirms "Connected".
- **Robot not yet on Wi-Fi:** the robot opens a temporary hotspot. The phone joins it and sends the home Wi-Fi name and password. The robot joins Wi-Fi, then continues as above.
- If anything fails: error message and Retry.

**After setup:**
- Robot sends a regular heartbeat with battery level, Wi-Fi status, and time. This drives the Robot Status card (connected/disconnected, Wi-Fi, battery, last active).
- Robot sends learning data (activities, answers, scores, progress). Home, Progress, and Activity read this from the backend.
- Robot fetches the parent's daily goals and tasks from the backend.

**Future:** Bluetooth scanning, QR scan, and Bluetooth + Wi-Fi setup will be added when the robot supports them. They need Bluetooth and camera permissions in the app.
