package com.roavai.parent

import com.roavai.parent.core.util.AppResult
import com.roavai.parent.data.fake.*
import com.roavai.parent.domain.model.ActivityPeriod
import com.roavai.parent.domain.model.Period
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class FoundationDataTest {

    private lateinit var clock: Clock
    private lateinit var zoneId: ZoneId
    private lateinit var sampleData: SampleData
    private lateinit var demoSettings: DemoSettings

    private lateinit var authRepo: FakeAuthRepository
    private lateinit var childRepo: FakeChildRepository
    private lateinit var robotRepo: FakeRobotRepository
    private lateinit var homeRepo: FakeHomeRepository
    private lateinit var activityRepo: FakeActivityRepository
    private lateinit var progressRepo: FakeProgressRepository
    private lateinit var goalsRepo: FakeGoalsRepository
    private lateinit var notificationsRepo: FakeNotificationsRepository

    @Before
    fun setUp() {
        zoneId = ZoneId.of("Asia/Kolkata")
        clock = Clock.fixed(Instant.parse("2026-10-10T12:00:00Z"), zoneId)
        sampleData = SampleData(clock, zoneId)
        demoSettings = DemoSettings()

        authRepo = FakeAuthRepository(sampleData, demoSettings)
        childRepo = FakeChildRepository(sampleData, demoSettings)
        robotRepo = FakeRobotRepository(sampleData, demoSettings)
        homeRepo = FakeHomeRepository(sampleData, demoSettings)
        activityRepo = FakeActivityRepository(sampleData, demoSettings)
        progressRepo = FakeProgressRepository(sampleData, demoSettings)
        goalsRepo = FakeGoalsRepository(sampleData, demoSettings)
        notificationsRepo = FakeNotificationsRepository(sampleData, demoSettings)
    }

    @Test
    fun sampleData_todayLearningSummary_matchesSpec() {
        val today = sampleData.todaySummary
        assertEquals(45, today.learningMinutes)
        assertEquals(3, today.topicsCovered)
        assertEquals(25, today.questionsAnswered)
        assertEquals(20, today.correct)
        assertEquals(5, today.incorrect)
    }

    @Test
    fun sampleData_activitiesCounts_matchesSpec() = runTest {
        val allResult = activityRepo.activities(ActivityPeriod.ALL_TIME)
        assertTrue(allResult is AppResult.Success)
        val all = (allResult as AppResult.Success).data
        assertEquals(17, all.size)

        val todayResult = activityRepo.activities(ActivityPeriod.TODAY)
        assertTrue(todayResult is AppResult.Success)
        val today = (todayResult as AppResult.Success).data
        assertEquals(3, today.size)

        val weekResult = activityRepo.activities(ActivityPeriod.THIS_WEEK)
        assertTrue(weekResult is AppResult.Success)
        val week = (weekResult as AppResult.Success).data
        assertEquals(14, week.size)
    }

    @Test
    fun sampleData_learningTime_matchesSpec() = runTest {
        val thisWeekResult = progressRepo.learningTime(Period.THIS_WEEK)
        assertTrue(thisWeekResult is AppResult.Success)
        val thisWeek = (thisWeekResult as AppResult.Success).data
        assertEquals(300, thisWeek.totalMinutes)

        val lastWeekResult = progressRepo.learningTime(Period.LAST_WEEK)
        assertTrue(lastWeekResult is AppResult.Success)
        val lastWeek = (lastWeekResult as AppResult.Success).data
        assertEquals(215, lastWeek.totalMinutes)
    }

    @Test
    fun sampleData_subjectProgress_matchesSpec() = runTest {
        val subjectsResult = progressRepo.subjects(Period.THIS_WEEK)
        assertTrue(subjectsResult is AppResult.Success)
        val subjects = (subjectsResult as AppResult.Success).data
        val subjectMap = subjects.associate { it.subject to it.percent }

        assertEquals(70, subjectMap["Mathematics"])
        assertEquals(60, subjectMap["Science"])
        assertEquals(50, subjectMap["Social Science"])
        assertEquals(60, subjectMap["English"])
    }

    @Test
    fun sampleData_tests_totalEight() = runTest {
        assertEquals(8, sampleData.tests.size)
    }

    @Test
    fun sampleData_credits_total140() = runTest {
        val creditsResult = progressRepo.credits()
        assertTrue(creditsResult is AppResult.Success)
        val credits = (creditsResult as AppResult.Success).data
        assertEquals(140, credits.total)
    }

    @Test
    fun sampleData_notifications_threeUnread() = runTest {
        val unreadResult = notificationsRepo.unreadCount()
        assertTrue(unreadResult is AppResult.Success)
        val count = (unreadResult as AppResult.Success).data
        assertEquals(3, count)
    }

    @Test
    fun sampleData_allDemoCodes_workCorrectly() = runTest {
        // Auth demo codes
        val validLogin = authRepo.login("parent@example.com", "Password123")
        assertTrue(validLogin is AppResult.Success)

        val invalidLogin = authRepo.login("wrong@example.com", "wrong")
        assertTrue(invalidLogin is AppResult.Failure)

        val takenSignUp = authRepo.signUp("Meera", "taken@example.com", null, "pass")
        assertTrue(takenSignUp is AppResult.Failure)

        val validSignUp = authRepo.signUp("Meera", "new@example.com", null, "pass")
        assertTrue(validSignUp is AppResult.Success)

        val validOtp = authRepo.verifyOtp("parent@example.com", "123456")
        assertTrue(validOtp is AppResult.Success)

        val invalidOtp = authRepo.verifyOtp("parent@example.com", "999999")
        assertTrue(invalidOtp is AppResult.Failure)

        // Robot pairing demo codes
        val codeSuccess = robotRepo.pair("WINI2026")
        assertTrue(codeSuccess is AppResult.Success)

        val codeExpired = robotRepo.pair("EXPIRED1")
        assertTrue(codeExpired is AppResult.Failure)

        val codeUsed = robotRepo.pair("USED0001")
        assertTrue(codeUsed is AppResult.Failure)

        val codeInvalid = robotRepo.pair("RANDOM_CODE")
        assertTrue(codeInvalid is AppResult.Failure)
    }

    @Test
    fun activityRepository_demoStates_workAsExpected() = runTest {
        // NORMAL
        demoSettings.setDemoState(DemoState.NORMAL)
        val normal = activityRepo.activities(ActivityPeriod.TODAY)
        assertTrue(normal is AppResult.Success)
        assertEquals(3, (normal as AppResult.Success).data.size)

        // EMPTY
        demoSettings.setDemoState(DemoState.EMPTY)
        val empty = activityRepo.activities(ActivityPeriod.TODAY)
        assertTrue(empty is AppResult.Success)
        assertTrue((empty as AppResult.Success).data.isEmpty())

        // ERROR
        demoSettings.setDemoState(DemoState.ERROR)
        val error = activityRepo.activities(ActivityPeriod.TODAY)
        assertTrue(error is AppResult.Failure)

        // OFFLINE
        demoSettings.setDemoState(DemoState.OFFLINE)
        val offline = activityRepo.activities(ActivityPeriod.TODAY)
        assertTrue(offline is AppResult.NoConnection)
    }

    @Test
    fun homeRepository_demoStates_workAsExpected() = runTest {
        // NORMAL
        demoSettings.setDemoState(DemoState.NORMAL)
        val normal = homeRepo.home()
        assertTrue(normal is AppResult.Success)
        val normalData = (normal as AppResult.Success).data
        assertNotNull(normalData.robot)
        assertEquals(45, normalData.today.learningMinutes)

        // EMPTY (single-object result: zero values, empty lists, robot = null)
        demoSettings.setDemoState(DemoState.EMPTY)
        val empty = homeRepo.home()
        assertTrue(empty is AppResult.Success)
        val emptyData = (empty as AppResult.Success).data
        assertNull(emptyData.robot)
        assertEquals(0, emptyData.today.learningMinutes)
        assertTrue(emptyData.recentActivity.isEmpty())
        assertTrue(emptyData.subjectProgress.isEmpty())

        // ERROR
        demoSettings.setDemoState(DemoState.ERROR)
        val error = homeRepo.home()
        assertTrue(error is AppResult.Failure)

        // OFFLINE
        demoSettings.setDemoState(DemoState.OFFLINE)
        val offline = homeRepo.home()
        assertTrue(offline is AppResult.NoConnection)
    }
}
