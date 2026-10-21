package com.roavai.parent.data.fake

import com.roavai.parent.core.util.AppResult
import com.roavai.parent.domain.model.*
import com.roavai.parent.domain.repository.*
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAuthRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<Parent> {
        return demoSettings.applyDemoState(
            emptyValue = { Parent("", "", "", null) }
        ) {
            if (email == "parent@example.com" && password == "Password123") {
                sampleData.isLoggedIn = true
                AppResult.Success(sampleData.currentParent!!)
            } else {
                AppResult.Failure("Debug: Invalid email or password")
            }
        }
    }

    override suspend fun signUp(fullName: String, email: String, mobile: String?, password: String): AppResult<Parent> {
        return demoSettings.applyDemoState(
            emptyValue = { Parent("", "", "", null) }
        ) {
            if (email == "taken@example.com") {
                AppResult.Failure("Debug: Email already registered")
            } else {
                val newParent = Parent("p_${System.currentTimeMillis()}", fullName, email, mobile)
                sampleData.currentParent = newParent
                sampleData.isLoggedIn = true
                AppResult.Success(newParent)
            }
        }
    }

    override suspend fun requestOtp(email: String): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            AppResult.Success(Unit)
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            if (otp == "123456") {
                AppResult.Success(Unit)
            } else {
                AppResult.Failure("Debug: Incorrect OTP")
            }
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            AppResult.Success(Unit)
        }
    }

    override suspend fun currentParent(): AppResult<Parent?> {
        return demoSettings.applyDemoState(
            emptyValue = { sampleData.currentParent }
        ) {
            AppResult.Success(sampleData.currentParent)
        }
    }

    override suspend fun isLoggedIn(): AppResult<Boolean> {
        return demoSettings.applyDemoState(
            emptyValue = { false }
        ) {
            AppResult.Success(sampleData.isLoggedIn)
        }
    }

    override suspend fun logout(): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            sampleData.isLoggedIn = false
            AppResult.Success(Unit)
        }
    }
}

@Singleton
class FakeChildRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : ChildRepository {

    override suspend fun addChild(name: String, grade: String, curriculum: String, consent: Boolean): AppResult<Child> {
        return demoSettings.applyDemoState(
            emptyValue = { Child("", "", "", "", null, 0) }
        ) {
            val updated = sampleData.currentChild.copy(
                name = name,
                grade = grade,
                curriculum = curriculum
            )
            sampleData.currentChild = updated
            AppResult.Success(updated)
        }
    }

    override suspend fun currentChild(): AppResult<Child> {
        return demoSettings.applyDemoState(
            emptyValue = { Child("", "", "", "", null, 0) }
        ) {
            AppResult.Success(sampleData.currentChild)
        }
    }
}

@Singleton
class FakeRobotRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : RobotRepository {

    override suspend fun pair(code: String): AppResult<RobotStatus> {
        return demoSettings.applyDemoState(
            emptyValue = { sampleData.robotStatusDisconnected.copy(connected = false, batteryPercent = 0) }
        ) {
            when (code) {
                "WINI2026" -> {
                    sampleData.isRobotPaired = true
                    sampleData.pairedRobotCode = code
                    demoSettings.setRobotDisconnected(false)
                    AppResult.Success(sampleData.robotStatusNormal)
                }
                "EXPIRED1" -> {
                    AppResult.Failure("Debug: This code has expired. Restart your robot to get a new one.")
                }
                "USED0001" -> {
                    AppResult.Failure("Debug: This code has already been used.")
                }
                else -> {
                    AppResult.Failure("Debug: Code not found. Check the code on your robot.")
                }
            }
        }
    }

    override suspend fun status(): AppResult<RobotStatus?> {
        return demoSettings.applyDemoState(
            emptyValue = { null }
        ) {
            if (!sampleData.isRobotPaired) {
                AppResult.Success(null)
            } else if (demoSettings.robotDisconnected.value) {
                AppResult.Success(sampleData.robotStatusDisconnected)
            } else {
                AppResult.Success(sampleData.robotStatusNormal)
            }
        }
    }

    override suspend fun unpair(): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            sampleData.isRobotPaired = false
            AppResult.Success(Unit)
        }
    }
}

@Singleton
class FakeHomeRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : HomeRepository {

    override suspend fun home(): AppResult<HomeData> {
        return demoSettings.applyDemoState(
            emptyValue = {
                // Return Success with zero values and empty lists, robot = null (Correction #3)
                HomeData(
                    child = sampleData.currentChild,
                    robot = null,
                    today = TodaySummary(0, sampleData.currentChild.dailyGoalMinutes, 0, emptyList(), 0, 0, 0, 0),
                    recentActivity = emptyList(),
                    subjectProgress = emptyList(),
                    upcomingGoals = emptyList(),
                    achievements = emptyList(),
                    unreadNotifications = 0
                )
            }
        ) {
            val robot = if (!sampleData.isRobotPaired) null
            else if (demoSettings.robotDisconnected.value) sampleData.robotStatusDisconnected
            else sampleData.robotStatusNormal

            val homeData = HomeData(
                child = sampleData.currentChild,
                robot = robot,
                today = sampleData.todaySummary,
                recentActivity = sampleData.activities.take(3),
                subjectProgress = sampleData.subjectProgressList,
                upcomingGoals = sampleData.goals,
                achievements = sampleData.achievements,
                unreadNotifications = sampleData.unreadNotificationsCount
            )
            AppResult.Success(homeData)
        }
    }
}

@Singleton
class FakeActivityRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : ActivityRepository {

    override suspend fun activities(period: ActivityPeriod, subject: String?): AppResult<List<Activity>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            val list = when (period) {
                ActivityPeriod.TODAY -> sampleData.activities.take(3)
                ActivityPeriod.THIS_WEEK -> sampleData.activities.take(14)
                ActivityPeriod.THIS_MONTH, ActivityPeriod.ALL_TIME -> sampleData.activities
            }
            val filtered = if (subject != null && !subject.equals("All Subjects", ignoreCase = true)) {
                list.filter { it.subject.equals(subject, ignoreCase = true) }
            } else {
                list
            }
            AppResult.Success(filtered)
        }
    }

    override suspend fun activity(id: String): AppResult<Activity?> {
        return demoSettings.applyDemoState(
            emptyValue = { null }
        ) {
            val found = sampleData.activities.firstOrNull { it.id == id }
            AppResult.Success(found)
        }
    }
}

@Singleton
class FakeProgressRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : ProgressRepository {

    override suspend fun overall(period: Period): AppResult<OverallProgress> {
        return demoSettings.applyDemoState(
            emptyValue = {
                OverallProgress(0, "Needs attention", 0, 0, emptyList())
            }
        ) {
            AppResult.Success(sampleData.overallProgressFor(period))
        }
    }

    override suspend fun learningTime(period: Period): AppResult<LearningTime> {
        return demoSettings.applyDemoState(
            emptyValue = {
                LearningTime(0, sampleData.currentChild.dailyGoalMinutes, emptyList())
            }
        ) {
            AppResult.Success(sampleData.learningTimeFor(period))
        }
    }

    override suspend fun subjects(period: Period): AppResult<List<SubjectProgress>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            AppResult.Success(sampleData.subjectProgressList)
        }
    }

    override suspend fun subjectTopics(subject: String): AppResult<List<TopicMastery>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            AppResult.Success(sampleData.topicsMasteryMap[subject] ?: emptyList())
        }
    }

    override suspend fun topics(subject: String?): AppResult<List<TopicMastery>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            val list = if (subject != null && !subject.equals("All Subjects", ignoreCase = true)) {
                sampleData.topicsMasteryMap[subject] ?: emptyList()
            } else {
                sampleData.topicsMasteryMap.values.flatten()
            }
            AppResult.Success(list)
        }
    }

    override suspend fun tests(period: Period): AppResult<List<TestResult>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            val all = sampleData.tests
            val list = when (period) {
                Period.THIS_WEEK -> all.subList(0, 3) // t1, t2, t3
                Period.LAST_WEEK -> all.subList(3, 6) // t4, t5, t6
                Period.THIS_MONTH -> all.subList(0, 6) // t1..t6
                Period.LAST_MONTH -> all.subList(6, 8) // t7, t8
            }
            AppResult.Success(list)
        }
    }

    override suspend fun comparisons(period: Period): AppResult<Comparisons> {
        return demoSettings.applyDemoState(
            emptyValue = {
                Comparisons(
                    ComparisonRow("Learning time", "0 min", "0 min", "no change"),
                    ComparisonRow("Overall progress", "0%", "0%", "no change"),
                    ComparisonRow("Test average", "0%", "0%", "no change"),
                    emptyList()
                )
            }
        ) {
            AppResult.Success(sampleData.comparisonsFor(period))
        }
    }

    override suspend fun credits(): AppResult<CreditSummary> {
        return demoSettings.applyDemoState(
            emptyValue = {
                CreditSummary(0, emptyList(), "")
            }
        ) {
            val summary = CreditSummary(
                total = 140,
                recent = sampleData.credits,
                motivation = "Great job! Aarav is on a 7-day streak."
            )
            AppResult.Success(summary)
        }
    }

    override suspend fun creditHistory(): AppResult<List<CreditEntry>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            AppResult.Success(sampleData.credits)
        }
    }
}

@Singleton
class FakeGoalsRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : GoalsRepository {

    override suspend fun goals(): AppResult<List<Goal>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            AppResult.Success(sampleData.goals)
        }
    }

    override suspend fun addGoal(
        title: String,
        subject: String,
        topic: String?,
        date: LocalDate,
        time: LocalTime?
    ): AppResult<Goal> {
        return demoSettings.applyDemoState(
            emptyValue = { Goal("", "", "", null, date, time, false) }
        ) {
            val created = sampleData.addGoal(title, subject, topic, date, time)
            AppResult.Success(created)
        }
    }

    override suspend fun updateGoal(goal: Goal): AppResult<Goal> {
        return demoSettings.applyDemoState(
            emptyValue = { goal }
        ) {
            val updated = sampleData.updateGoal(goal)
            AppResult.Success(updated)
        }
    }

    override suspend fun deleteGoal(id: String): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            sampleData.deleteGoal(id)
            AppResult.Success(Unit)
        }
    }

    override suspend fun setDailyGoal(minutes: Int): AppResult<Int> {
        return demoSettings.applyDemoState(
            emptyValue = { 0 }
        ) {
            val updated = sampleData.setDailyGoal(minutes)
            AppResult.Success(updated)
        }
    }
}

@Singleton
class FakeNotificationsRepository @Inject constructor(
    private val sampleData: SampleData,
    private val demoSettings: DemoSettings
) : NotificationsRepository {

    override suspend fun notifications(): AppResult<List<AppNotification>> {
        return demoSettings.applyDemoState(
            emptyValue = { emptyList() }
        ) {
            AppResult.Success(sampleData.notifications)
        }
    }

    override suspend fun markRead(id: String): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            sampleData.markNotificationRead(id)
            AppResult.Success(Unit)
        }
    }

    override suspend fun markAllRead(): AppResult<Unit> {
        return demoSettings.applyDemoState(
            emptyValue = { Unit }
        ) {
            sampleData.markAllNotificationsRead()
            AppResult.Success(Unit)
        }
    }

    override suspend fun unreadCount(): AppResult<Int> {
        return demoSettings.applyDemoState(
            emptyValue = { 0 }
        ) {
            AppResult.Success(sampleData.unreadNotificationsCount)
        }
    }
}
