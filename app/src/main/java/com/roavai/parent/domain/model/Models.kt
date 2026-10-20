package com.roavai.parent.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

data class Parent(
    val id: String,
    val fullName: String,
    val email: String,
    val mobile: String?
)

data class Child(
    val id: String,
    val name: String,
    val grade: String,
    val curriculum: String,
    val avatarUrl: String?,
    val dailyGoalMinutes: Int
)

enum class WifiSignal { STRONG, MEDIUM, WEAK }

data class RobotStatus(
    val connected: Boolean,
    val wifiConnected: Boolean,
    val wifiSignal: WifiSignal,
    val batteryPercent: Int,
    val lastActive: Instant?,
    val firmwareVersion: String?
)

enum class ActivityType { PRACTICE, STUDY, QUIZ, LESSON, READING, EDUCATIONAL_ACTIVITY }
enum class ActivityStatus { COMPLETED, IN_PROGRESS }

data class Activity(
    val id: String,
    val title: String,
    val subject: String,
    val topic: String,
    val summary: String,
    val type: ActivityType,
    val status: ActivityStatus,
    val startedAt: Instant,
    val durationMinutes: Int,
    val questionsAnswered: Int? = null,
    val correctQuestions: Int? = null,
    val incorrectQuestions: Int? = null
)

data class TodaySummary(
    val learningMinutes: Int,
    val dailyGoalMinutes: Int,
    val topicsCovered: Int,
    val subjects: List<String>,
    val questionsAnswered: Int,
    val correct: Int,
    val incorrect: Int,
    val overallProgressPercent: Int
)

data class SubjectProgress(
    val subject: String,
    val percent: Int,
    val completedTopics: Int,
    val totalTopics: Int,
    val needsPracticeTopics: Int
)

data class Goal(
    val id: String,
    val title: String,
    val subject: String,
    val topic: String?,
    val date: LocalDate,
    val time: LocalTime?,
    val done: Boolean
)

enum class AchievementKind { STREAK, CONSISTENT_LEARNING, SUBJECT_ACHIEVEMENT, MILESTONE }

data class Achievement(
    val id: String,
    val kind: AchievementKind,
    val title: String,
    val description: String,
    val earnedAt: Instant
)

data class HomeData(
    val child: Child,
    val robot: RobotStatus?,
    val today: TodaySummary,
    val recentActivity: List<Activity>,
    val subjectProgress: List<SubjectProgress>,
    val upcomingGoals: List<Goal>,
    val achievements: List<Achievement>,
    val unreadNotifications: Int
)

enum class Period { THIS_WEEK, LAST_WEEK, THIS_MONTH, LAST_MONTH }
enum class ActivityPeriod { TODAY, THIS_WEEK, THIS_MONTH, ALL_TIME }

data class DayValue(val date: LocalDate, val value: Int)

data class OverallProgress(
    val percent: Int,
    val statusLabel: String,
    val completedActivities: Int,
    val inProgressActivities: Int,
    val perDay: List<DayValue>
)

data class LearningTime(
    val totalMinutes: Int,
    val dailyGoalMinutes: Int,
    val perDay: List<DayValue>
)

enum class TopicStatus { COMPLETED, LEARNING, NEEDS_PRACTICE }

data class TopicMastery(
    val subject: String,
    val topic: String,
    val percent: Int,
    val status: TopicStatus
)

data class TestResult(
    val id: String,
    val name: String,
    val subject: String,
    val date: LocalDate,
    val score: Int,
    val total: Int,
    val correct: Int,
    val incorrect: Int,
    val percentage: Int
)

data class ComparisonRow(
    val label: String,
    val current: String,
    val previous: String,
    val change: String
)

data class Comparisons(
    val learningTime: ComparisonRow,
    val overallProgress: ComparisonRow,
    val testAverage: ComparisonRow,
    val subjects: List<ComparisonRow>
)

data class CreditEntry(
    val id: String,
    val amount: Int,
    val reason: String,
    val activityTitle: String?,
    val achievementTitle: String?,
    val earnedAt: Instant
)

data class CreditSummary(
    val total: Int,
    val recent: List<CreditEntry>,
    val motivation: String
)

enum class NotificationType { LEARNING_UPDATE, ROBOT_UPDATE, IMPORTANT, ACHIEVEMENT }

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val read: Boolean,
    val createdAt: Instant
)
