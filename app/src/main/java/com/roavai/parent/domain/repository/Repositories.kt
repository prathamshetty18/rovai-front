package com.roavai.parent.domain.repository

import com.roavai.parent.core.util.AppResult
import com.roavai.parent.domain.model.*
import java.time.LocalDate
import java.time.LocalTime

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<Parent>
    suspend fun signUp(fullName: String, email: String, mobile: String?, password: String): AppResult<Parent>
    suspend fun requestOtp(email: String): AppResult<Unit>
    suspend fun verifyOtp(email: String, otp: String): AppResult<Unit>
    suspend fun resetPassword(email: String, newPassword: String): AppResult<Unit>
    suspend fun currentParent(): AppResult<Parent?>
    suspend fun isLoggedIn(): AppResult<Boolean>
    suspend fun logout(): AppResult<Unit>
}

interface ChildRepository {
    suspend fun addChild(name: String, grade: String, curriculum: String, consent: Boolean): AppResult<Child>
    suspend fun currentChild(): AppResult<Child>
}

interface RobotRepository {
    suspend fun pair(code: String): AppResult<RobotStatus>
    suspend fun status(): AppResult<RobotStatus?>
    suspend fun unpair(): AppResult<Unit>
}

interface HomeRepository {
    suspend fun home(): AppResult<HomeData>
}

interface ActivityRepository {
    suspend fun activities(period: ActivityPeriod, subject: String? = null): AppResult<List<Activity>>
    suspend fun activity(id: String): AppResult<Activity?>
}

interface ProgressRepository {
    suspend fun overall(period: Period): AppResult<OverallProgress>
    suspend fun learningTime(period: Period): AppResult<LearningTime>
    suspend fun subjects(period: Period): AppResult<List<SubjectProgress>>
    suspend fun subjectTopics(subject: String): AppResult<List<TopicMastery>>
    suspend fun topics(subject: String? = null): AppResult<List<TopicMastery>>
    suspend fun tests(period: Period): AppResult<List<TestResult>>
    suspend fun comparisons(period: Period): AppResult<Comparisons>
    suspend fun credits(): AppResult<CreditSummary>
    suspend fun creditHistory(): AppResult<List<CreditEntry>>
}

interface GoalsRepository {
    suspend fun goals(): AppResult<List<Goal>>
    suspend fun addGoal(
        title: String,
        subject: String,
        topic: String?,
        date: LocalDate,
        time: LocalTime?
    ): AppResult<Goal>
    suspend fun updateGoal(goal: Goal): AppResult<Goal>
    suspend fun deleteGoal(id: String): AppResult<Unit>
    suspend fun setDailyGoal(minutes: Int): AppResult<Int>
}

interface NotificationsRepository {
    suspend fun notifications(): AppResult<List<AppNotification>>
    suspend fun markRead(id: String): AppResult<Unit>
    suspend fun markAllRead(): AppResult<Unit>
    suspend fun unreadCount(): AppResult<Int>
}
