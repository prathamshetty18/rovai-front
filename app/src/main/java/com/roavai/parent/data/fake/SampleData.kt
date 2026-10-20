package com.roavai.parent.data.fake

import com.roavai.parent.domain.model.*
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleData @Inject constructor(
    private val clock: Clock,
    private val zoneId: ZoneId
) {
    val today: LocalDate
        get() = LocalDate.now(clock.withZone(zoneId))

    val now: Instant
        get() = Instant.now(clock)

    fun d(offsetDays: Long): LocalDate = today.plusDays(offsetDays)

    fun atTime(date: LocalDate, hour: Int, minute: Int): Instant {
        return date.atTime(hour, minute).atZone(zoneId).toInstant()
    }

    // In-memory states
    var isLoggedIn: Boolean = false
    var currentParent: Parent? = Parent(
        id = "p1",
        fullName = "Meera D'Souza",
        email = "parent@example.com",
        mobile = "+91 90000 00000"
    )

    var currentChild: Child = Child(
        id = "c1",
        name = "Aarav D'Souza",
        grade = "Class 10",
        curriculum = "CBSE",
        avatarUrl = null,
        dailyGoalMinutes = 60
    )

    var isRobotPaired: Boolean = true
    var pairedRobotCode: String = "WINI2026"

    val robotStatusNormal: RobotStatus
        get() = RobotStatus(
            connected = true,
            wifiConnected = true,
            wifiSignal = WifiSignal.STRONG,
            batteryPercent = 82,
            lastActive = now.minusSeconds(5 * 60),
            firmwareVersion = "1.0.3"
        )

    val robotStatusDisconnected: RobotStatus
        get() = RobotStatus(
            connected = false,
            wifiConnected = false,
            wifiSignal = WifiSignal.WEAK,
            batteryPercent = 82,
            lastActive = now.minusSeconds(3 * 3600),
            firmwareVersion = "1.0.3"
        )

    val todaySummary: TodaySummary
        get() = TodaySummary(
            learningMinutes = 45,
            dailyGoalMinutes = currentChild.dailyGoalMinutes,
            topicsCovered = 3,
            subjects = listOf("Mathematics", "Science", "English"),
            questionsAnswered = 25,
            correct = 20,
            incorrect = 5,
            overallProgressPercent = 60
        )

    val activities: List<Activity>
        get() = listOf(
            Activity(
                id = "a01",
                title = "Mathematics: Quadratic Equations",
                subject = "Mathematics",
                topic = "Quadratic Equations",
                summary = "Completed 15 questions",
                type = ActivityType.PRACTICE,
                status = ActivityStatus.COMPLETED,
                startedAt = now.minusSeconds(3600),
                durationMinutes = 15,
                questionsAnswered = 15,
                correctQuestions = 12,
                incorrectQuestions = 3
            ),
            Activity(
                id = "a02",
                title = "Chemistry: Acids, Bases & Salts",
                subject = "Science",
                topic = "Acids, Bases & Salts",
                summary = "Watched video + Notes",
                type = ActivityType.STUDY,
                status = ActivityStatus.COMPLETED,
                startedAt = now.minusSeconds(7200),
                durationMinutes = 15
            ),
            Activity(
                id = "a03",
                title = "English: Grammar Practice",
                subject = "English",
                topic = "Grammar",
                summary = "Answered 10 questions",
                type = ActivityType.PRACTICE,
                status = ActivityStatus.COMPLETED,
                startedAt = now.minusSeconds(10800),
                durationMinutes = 15,
                questionsAnswered = 10,
                correctQuestions = 8,
                incorrectQuestions = 2
            ),
            Activity(
                id = "a04",
                title = "Physics: Light: Reflection & Refraction",
                subject = "Science",
                topic = "Light: Reflection & Refraction",
                summary = "Answered 10 questions",
                type = ActivityType.PRACTICE,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-1), 19, 45),
                durationMinutes = 20,
                questionsAnswered = 10,
                correctQuestions = 8,
                incorrectQuestions = 2
            ),
            Activity(
                id = "a05",
                title = "English: Reading Comprehension",
                subject = "English",
                topic = "Reading Comprehension",
                summary = "Read a passage and answered 5 questions",
                type = ActivityType.READING,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-1), 17, 10),
                durationMinutes = 20,
                questionsAnswered = 5,
                correctQuestions = 4,
                incorrectQuestions = 1
            ),
            Activity(
                id = "a06",
                title = "Social Science: Nationalism in India",
                subject = "Social Science",
                topic = "Nationalism in India",
                summary = "Finished lesson 3",
                type = ActivityType.LESSON,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-1), 16, 0),
                durationMinutes = 20
            ),
            Activity(
                id = "a07",
                title = "Mathematics: Trigonometry Quiz",
                subject = "Mathematics",
                topic = "Trigonometry",
                summary = "Quiz: 20 questions",
                type = ActivityType.QUIZ,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-2), 18, 20),
                durationMinutes = 25,
                questionsAnswered = 20,
                correctQuestions = 16,
                incorrectQuestions = 4
            ),
            Activity(
                id = "a08",
                title = "Science: Life Processes",
                subject = "Science",
                topic = "Life Processes",
                summary = "Started a plant cell activity",
                type = ActivityType.EDUCATIONAL_ACTIVITY,
                status = ActivityStatus.IN_PROGRESS,
                startedAt = atTime(d(-2), 16, 30),
                durationMinutes = 30
            ),
            Activity(
                id = "a09",
                title = "Social Science: Resources and Development",
                subject = "Social Science",
                topic = "Resources and Development",
                summary = "Read 2 pages",
                type = ActivityType.READING,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-3), 17, 0),
                durationMinutes = 20
            ),
            Activity(
                id = "a10",
                title = "Science: Life Processes Quiz",
                subject = "Science",
                topic = "Life Processes",
                summary = "Quiz: 20 questions",
                type = ActivityType.QUIZ,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-4), 18, 0),
                durationMinutes = 25,
                questionsAnswered = 20,
                correctQuestions = 14,
                incorrectQuestions = 6
            ),
            Activity(
                id = "a11",
                title = "English: Letter Writing",
                subject = "English",
                topic = "Letter Writing",
                summary = "Practised a formal letter",
                type = ActivityType.LESSON,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-4), 15, 30),
                durationMinutes = 15
            ),
            Activity(
                id = "a12",
                title = "Mathematics: Linear Equations",
                subject = "Mathematics",
                topic = "Pair of Linear Equations",
                summary = "Answered 20 questions",
                type = ActivityType.PRACTICE,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-5), 18, 30),
                durationMinutes = 30,
                questionsAnswered = 20,
                correctQuestions = 17,
                incorrectQuestions = 3
            ),
            Activity(
                id = "a13",
                title = "Science: Electricity",
                subject = "Science",
                topic = "Electricity",
                summary = "Watched video",
                type = ActivityType.STUDY,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-5), 16, 0),
                durationMinutes = 20
            ),
            Activity(
                id = "a14",
                title = "Social Science: Nationalism in India Quiz",
                subject = "Social Science",
                topic = "Nationalism in India",
                summary = "Quiz: 15 questions",
                type = ActivityType.QUIZ,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-6), 17, 0),
                durationMinutes = 30,
                questionsAnswered = 15,
                correctQuestions = 9,
                incorrectQuestions = 6
            ),
            Activity(
                id = "a15",
                title = "English: Grammar Quiz",
                subject = "English",
                topic = "Grammar",
                summary = "Quiz: 20 questions",
                type = ActivityType.QUIZ,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-8), 18, 0),
                durationMinutes = 25,
                questionsAnswered = 20,
                correctQuestions = 17,
                incorrectQuestions = 3
            ),
            Activity(
                id = "a16",
                title = "Science: Chemical Reactions Quiz",
                subject = "Science",
                topic = "Chemical Reactions",
                summary = "Quiz: 20 questions",
                type = ActivityType.QUIZ,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-11), 17, 30),
                durationMinutes = 25,
                questionsAnswered = 20,
                correctQuestions = 12,
                incorrectQuestions = 8
            ),
            Activity(
                id = "a17",
                title = "Mathematics: Linear Equations Quiz",
                subject = "Mathematics",
                topic = "Pair of Linear Equations",
                summary = "Quiz: 20 questions",
                type = ActivityType.QUIZ,
                status = ActivityStatus.COMPLETED,
                startedAt = atTime(d(-13), 16, 30),
                durationMinutes = 25,
                questionsAnswered = 20,
                correctQuestions = 18,
                incorrectQuestions = 2
            )
        )

    val subjectProgressList: List<SubjectProgress> = listOf(
        SubjectProgress("Mathematics", 70, 7, 10, 1),
        SubjectProgress("Science", 60, 6, 10, 2),
        SubjectProgress("Social Science", 50, 5, 10, 2),
        SubjectProgress("English", 60, 6, 10, 1)
    )

    val topicsMasteryMap: Map<String, List<TopicMastery>> = mapOf(
        "Mathematics" to listOf(
            TopicMastery("Mathematics", "Real Numbers", 92, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Polynomials", 88, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Pair of Linear Equations", 90, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Quadratic Equations", 85, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Arithmetic Progressions", 83, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Triangles", 81, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Coordinate Geometry", 80, TopicStatus.COMPLETED),
            TopicMastery("Mathematics", "Trigonometry", 72, TopicStatus.LEARNING),
            TopicMastery("Mathematics", "Circles", 65, TopicStatus.LEARNING),
            TopicMastery("Mathematics", "Statistics", 42, TopicStatus.NEEDS_PRACTICE)
        ),
        "Science" to listOf(
            TopicMastery("Science", "Chemical Reactions", 86, TopicStatus.COMPLETED),
            TopicMastery("Science", "Acids, Bases & Salts", 84, TopicStatus.COMPLETED),
            TopicMastery("Science", "Metals and Non-metals", 82, TopicStatus.COMPLETED),
            TopicMastery("Science", "Life Processes", 80, TopicStatus.COMPLETED),
            TopicMastery("Science", "Control and Coordination", 81, TopicStatus.COMPLETED),
            TopicMastery("Science", "Heredity", 80, TopicStatus.COMPLETED),
            TopicMastery("Science", "Light: Reflection & Refraction", 68, TopicStatus.LEARNING),
            TopicMastery("Science", "Electricity", 60, TopicStatus.LEARNING),
            TopicMastery("Science", "Carbon and its Compounds", 45, TopicStatus.NEEDS_PRACTICE),
            TopicMastery("Science", "Magnetic Effects of Electric Current", 40, TopicStatus.NEEDS_PRACTICE)
        ),
        "Social Science" to listOf(
            TopicMastery("Social Science", "Rise of Nationalism in Europe", 85, TopicStatus.COMPLETED),
            TopicMastery("Social Science", "Nationalism in India", 82, TopicStatus.COMPLETED),
            TopicMastery("Social Science", "Resources and Development", 84, TopicStatus.COMPLETED),
            TopicMastery("Social Science", "Agriculture", 80, TopicStatus.COMPLETED),
            TopicMastery("Social Science", "Water Resources", 81, TopicStatus.COMPLETED),
            TopicMastery("Social Science", "Minerals and Energy", 62, TopicStatus.LEARNING),
            TopicMastery("Social Science", "Print Culture", 58, TopicStatus.LEARNING),
            TopicMastery("Social Science", "Power Sharing", 55, TopicStatus.LEARNING),
            TopicMastery("Social Science", "Federalism", 45, TopicStatus.NEEDS_PRACTICE),
            TopicMastery("Social Science", "Money and Credit", 40, TopicStatus.NEEDS_PRACTICE)
        ),
        "English" to listOf(
            TopicMastery("English", "Letter Writing", 88, TopicStatus.COMPLETED),
            TopicMastery("English", "Reading Comprehension", 85, TopicStatus.COMPLETED),
            TopicMastery("English", "Grammar", 82, TopicStatus.COMPLETED),
            TopicMastery("English", "Notice Writing", 84, TopicStatus.COMPLETED),
            TopicMastery("English", "Formal Email", 81, TopicStatus.COMPLETED),
            TopicMastery("English", "Dialogue Writing", 80, TopicStatus.COMPLETED),
            TopicMastery("English", "Story Writing", 66, TopicStatus.LEARNING),
            TopicMastery("English", "Report Writing", 60, TopicStatus.LEARNING),
            TopicMastery("English", "Prose Comprehension", 55, TopicStatus.LEARNING),
            TopicMastery("English", "Poetry Analysis", 47, TopicStatus.NEEDS_PRACTICE)
        )
    )

    val tests: List<TestResult>
        get() = listOf(
            TestResult("t1", "Trigonometry Quiz", "Mathematics", d(-2), 16, 20, 16, 4, 80),
            TestResult("t2", "Life Processes Quiz", "Science", d(-4), 14, 20, 14, 6, 70),
            TestResult("t3", "Nationalism in India Quiz", "Social Science", d(-6), 9, 15, 9, 6, 60),
            TestResult("t4", "Grammar Quiz", "English", d(-8), 17, 20, 17, 3, 85),
            TestResult("t5", "Chemical Reactions Quiz", "Science", d(-11), 12, 20, 12, 8, 60),
            TestResult("t6", "Linear Equations Quiz", "Mathematics", d(-13), 18, 20, 18, 2, 90),
            TestResult("t7", "Quadratic Equations Quiz", "Mathematics", d(-35), 15, 20, 15, 5, 75),
            TestResult("t8", "Light Quiz", "Science", d(-40), 13, 20, 13, 7, 65)
        )

    val credits: List<CreditEntry>
        get() = listOf(
            CreditEntry("c1", 10, "Great Effort", "Mathematics: Trigonometry Quiz", null, atTime(d(-1), 18, 0)),
            CreditEntry("c2", 15, "Consistent Learner", null, "5-day learning streak", atTime(d(-2), 18, 0)),
            CreditEntry("c3", 20, "Topic Master", "Mathematics: Quadratic Equations", "Topic Master: Quadratic Equations", atTime(d(-3), 18, 0)),
            CreditEntry("c4", 10, "Great Effort", "Mathematics: Linear Equations", null, atTime(d(-5), 18, 0)),
            CreditEntry("c5", 10, "Great Effort", "Social Science: Nationalism in India Quiz", null, atTime(d(-6), 18, 0)),
            CreditEntry("c6", 20, "Topic Master", "English: Grammar Quiz", "Topic Master: Grammar", atTime(d(-8), 18, 0)),
            CreditEntry("c7", 10, "Great Effort", null, null, atTime(d(-9), 18, 0)),
            CreditEntry("c8", 15, "Consistent Learner", null, "3-day learning streak", atTime(d(-11), 18, 0)),
            CreditEntry("c9", 10, "Great Effort", null, null, atTime(d(-12), 18, 0)),
            CreditEntry("c10", 20, "Topic Master", "Science: Chemical Reactions Quiz", "Topic Master: Chemical Reactions", atTime(d(-14), 18, 0))
        )

    val achievements: List<Achievement>
        get() = listOf(
            Achievement("ac1", AchievementKind.STREAK, "7-day learning streak", "Learned every day this week", atTime(d(0), 12, 0)),
            Achievement("ac2", AchievementKind.SUBJECT_ACHIEVEMENT, "Topic Master: Quadratic Equations", "Mastered a Mathematics topic", atTime(d(-3), 12, 0)),
            Achievement("ac3", AchievementKind.CONSISTENT_LEARNING, "Consistent Learner", "Learned 5 days in a row", atTime(d(-2), 12, 0)),
            Achievement("ac4", AchievementKind.MILESTONE, "100 questions answered", "Answered 100 practice questions", atTime(d(-9), 12, 0)),
            Achievement("ac5", AchievementKind.STREAK, "3-day learning streak", "Learned 3 days in a row", atTime(d(-12), 12, 0))
        )

    // Mutable in-memory upcoming goals
    private val _goals = mutableListOf(
        Goal("g1", "Practice Algebra", "Mathematics", "Pair of Linear Equations", d(1), LocalTime.of(17, 0), false),
        Goal("g2", "Revise Acids, Bases & Salts", "Science", "Acids, Bases & Salts", d(2), LocalTime.of(16, 30), false),
        Goal("g3", "Read Chapter 4", "English", "Reading Comprehension", d(3), LocalTime.of(18, 0), false)
    )

    val goals: List<Goal>
        get() = _goals.toList()

    fun addGoal(title: String, subject: String, topic: String?, date: LocalDate, time: LocalTime?): Goal {
        val newGoal = Goal("g_${UUID.randomUUID()}", title, subject, topic, date, time, false)
        _goals.add(newGoal)
        return newGoal
    }

    fun updateGoal(goal: Goal): Goal {
        val index = _goals.indexOfFirst { it.id == goal.id }
        if (index != -1) {
            _goals[index] = goal
        } else {
            _goals.add(goal)
        }
        return goal
    }

    fun deleteGoal(id: String) {
        _goals.removeAll { it.id == id }
    }

    fun setDailyGoal(minutes: Int): Int {
        currentChild = currentChild.copy(dailyGoalMinutes = minutes)
        return minutes
    }

    // Mutable in-memory notifications
    private val _notifications = mutableListOf(
        AppNotification("n1", "New achievement", "Aarav reached a 7-day learning streak.", NotificationType.ACHIEVEMENT, false, now.minusSeconds(2 * 3600)),
        AppNotification("n2", "Today's learning", "Aarav completed 3 activities today.", NotificationType.LEARNING_UPDATE, false, now.minusSeconds(5 * 3600)),
        AppNotification("n3", "Robot update", "Wini software 1.0.3 was installed.", NotificationType.ROBOT_UPDATE, false, atTime(d(-1), 12, 0)),
        AppNotification("n4", "Welcome to ROAVAI", "Your child's learning progress will appear here.", NotificationType.IMPORTANT, true, atTime(d(-2), 12, 0)),
        AppNotification("n5", "Topic Master", "Aarav mastered Quadratic Equations.", NotificationType.ACHIEVEMENT, true, atTime(d(-3), 12, 0))
    )

    val notifications: List<AppNotification>
        get() = _notifications.toList()

    fun markNotificationRead(id: String) {
        val index = _notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _notifications[index]
            _notifications[index] = item.copy(read = true)
        }
    }

    fun markAllNotificationsRead() {
        for (i in _notifications.indices) {
            _notifications[i] = _notifications[i].copy(read = true)
        }
    }

    val unreadNotificationsCount: Int
        get() = _notifications.count { !it.read }

    fun learningTimeFor(period: Period): LearningTime {
        return when (period) {
            Period.THIS_WEEK -> {
                val values = listOf(30, 50, 40, 20, 55, 60, 45)
                val days = (-6L..0L).mapIndexed { i, offset -> DayValue(d(offset), values[i]) }
                LearningTime(300, currentChild.dailyGoalMinutes, days)
            }
            Period.LAST_WEEK -> {
                val values = listOf(25, 40, 35, 30, 0, 45, 40)
                val days = (-13L..-7L).mapIndexed { i, offset -> DayValue(d(offset), values[i]) }
                LearningTime(215, currentChild.dailyGoalMinutes, days)
            }
            Period.THIS_MONTH -> {
                val days = (-29L..0L).map { offset ->
                    val min = if (offset < -9L) 39 else 40
                    DayValue(d(offset), min)
                }
                LearningTime(1180, currentChild.dailyGoalMinutes, days)
            }
            Period.LAST_MONTH -> {
                val days = (-59L..-30L).map { offset ->
                    val min = if (offset < -49L) 34 else 35
                    DayValue(d(offset), min)
                }
                LearningTime(1040, currentChild.dailyGoalMinutes, days)
            }
        }
    }

    fun overallProgressFor(period: Period): OverallProgress {
        return when (period) {
            Period.THIS_WEEK -> {
                val values = listOf(55, 56, 57, 58, 58, 59, 60)
                val days = (-6L..0L).mapIndexed { i, offset -> DayValue(d(offset), values[i]) }
                OverallProgress(60, "Keep going", 13, 1, days)
            }
            Period.LAST_WEEK -> {
                val values = listOf(50, 51, 52, 53, 54, 54, 55)
                val days = (-13L..-7L).mapIndexed { i, offset -> DayValue(d(offset), values[i]) }
                OverallProgress(55, "Keep going", 11, 1, days)
            }
            Period.THIS_MONTH -> {
                val days = (-29L..0L).mapIndexed { i, offset ->
                    val v = 48 + (12 * i) / 29
                    DayValue(d(offset), v)
                }
                OverallProgress(60, "Keep going", 38, 3, days)
            }
            Period.LAST_MONTH -> {
                val days = (-59L..-30L).mapIndexed { i, offset ->
                    val v = 40 + (8 * i) / 29
                    DayValue(d(offset), v)
                }
                OverallProgress(48, "Keep going", 31, 2, days)
            }
        }
    }

    fun comparisonsFor(period: Period): Comparisons {
        return when (period) {
            Period.THIS_WEEK -> Comparisons(
                learningTime = ComparisonRow("Learning time", "300 min", "215 min", "up 85 min"),
                overallProgress = ComparisonRow("Overall progress", "60%", "55%", "up 5 points"),
                testAverage = ComparisonRow("Test average", "70%", "78%", "down 8 points"),
                subjects = listOf(
                    ComparisonRow("Mathematics", "70%", "66%", "up 4"),
                    ComparisonRow("Science", "60%", "56%", "up 4"),
                    ComparisonRow("Social Science", "50%", "50%", "no change"),
                    ComparisonRow("English", "60%", "48%", "up 12")
                )
            )
            Period.LAST_WEEK -> Comparisons(
                learningTime = ComparisonRow("Learning time", "215 min", "190 min", "up 25 min"),
                overallProgress = ComparisonRow("Overall progress", "55%", "53%", "up 2 points"),
                testAverage = ComparisonRow("Test average", "78%", "72%", "up 6 points"),
                subjects = listOf(
                    ComparisonRow("Mathematics", "66%", "63%", "up 3"),
                    ComparisonRow("Science", "56%", "54%", "up 2"),
                    ComparisonRow("Social Science", "50%", "49%", "up 1"),
                    ComparisonRow("English", "48%", "46%", "up 2")
                )
            )
            Period.THIS_MONTH -> Comparisons(
                learningTime = ComparisonRow("Learning time", "1180 min", "1040 min", "up 140 min"),
                overallProgress = ComparisonRow("Overall progress", "60%", "48%", "up 12 points"),
                testAverage = ComparisonRow("Test average", "74%", "70%", "up 4 points"),
                subjects = listOf(
                    ComparisonRow("Mathematics", "70%", "60%", "up 10"),
                    ComparisonRow("Science", "60%", "50%", "up 10"),
                    ComparisonRow("Social Science", "50%", "44%", "up 6"),
                    ComparisonRow("English", "60%", "38%", "up 22")
                )
            )
            Period.LAST_MONTH -> Comparisons(
                learningTime = ComparisonRow("Learning time", "1040 min", "900 min", "up 140 min"),
                overallProgress = ComparisonRow("Overall progress", "48%", "40%", "up 8 points"),
                testAverage = ComparisonRow("Test average", "70%", "66%", "up 4 points"),
                subjects = listOf(
                    ComparisonRow("Mathematics", "60%", "52%", "up 8"),
                    ComparisonRow("Science", "50%", "42%", "up 8"),
                    ComparisonRow("Social Science", "44%", "38%", "up 6"),
                    ComparisonRow("English", "38%", "28%", "up 10")
                )
            )
        }
    }
}
