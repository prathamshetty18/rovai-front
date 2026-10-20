package com.roavai.parent.ui.navigation

object NavRoutes {
    // Flow routes
    const val SPLASH = "splash"
    const val ROBOT_SETUP = "robot_setup"
    const val ONBOARDING = "onboarding/{page}"
    const val SIGNUP = "signup"
    const val LOGIN = "login"
    const val FORGOT_EMAIL = "forgot/email"
    const val FORGOT_OTP = "forgot/otp"
    const val FORGOT_NEW_PASSWORD = "forgot/new_password"
    const val ADD_CHILD = "add_child"

    // Bottom tab routes
    const val HOME = "home"
    const val PROGRESS = "progress"
    const val ACTIVITY = "activity"
    const val ROBOT = "robot"
    const val PROFILE = "profile"

    // Progress detail routes
    const val PROGRESS_OVERALL = "progress/overall"
    const val PROGRESS_LEARNING_TIME = "progress/learning_time"
    const val PROGRESS_SUBJECTS = "progress/subjects"
    const val PROGRESS_SUBJECT_DETAIL = "progress/subjects/{subject}"
    const val PROGRESS_TOPICS = "progress/topics"
    const val PROGRESS_TESTS = "progress/tests"
    const val PROGRESS_COMPARISONS = "progress/comparisons"
    const val PROGRESS_CREDITS = "progress/credits"

    // Other detail routes
    const val ACTIVITY_DETAIL = "activity/{id}"
    const val GOALS_EDIT = "goals/edit"
    const val NOTIFICATIONS = "notifications"
    const val ACHIEVEMENTS = "achievements"

    val TAB_ROUTES = setOf(HOME, PROGRESS, ACTIVITY, ROBOT, PROFILE)
}
