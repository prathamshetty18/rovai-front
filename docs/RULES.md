# Project Rules (read before every task)

These rules apply to every task in this repo.

## Scope
1. Follow `docs/PRD.md`, `docs/FRONTEND_SPEC.md` and `docs/SAMPLE_DATA.md` only. Do not add features that are not in them.
2. Do ONLY the step you are given. Do not build anything else. Do not start the next step.
3. Show an implementation plan first and wait for approval before editing files.
4. Work only on the current git branch. Never commit to or merge into `main`.

## Frontend phase limits
5. **No networking.** Do not add Retrofit, OkHttp, Room, Firebase, WorkManager or any server call. All data comes from the fake repositories that return `docs/SAMPLE_DATA.md` data.
6. Use Kotlin, Jetpack Compose, Material 3, Hilt, Navigation Compose. Package `com.roavai.parent`. Min SDK 26.
7. Keep layers as in `FRONTEND_SPEC.md`: UI (Compose) -> ViewModel -> Repository interface -> Fake repository.
8. Every list or data screen supports the five states: Loading, Success, Empty, Error (with Retry) and Offline.

## Code quality
9. Composables are stateless where possible. State lives in the ViewModel as one `UiState` per screen.
10. No hardcoded user-visible text in code. Put all text in `strings.xml`.
11. No hardcoded colors or sizes in screens. Use the theme and shared components.
12. Every screen has a `@Preview` for the Success state.
13. Accessibility: content descriptions on icons and images, touch targets at least 48dp, text scales with system font size.
14. Do not leave unused code, TODOs without a note, or commented-out blocks.

## Safety and honesty
15. No secrets in code or replies. Do not create or edit `google-services.json`, keystores or `.env`.
16. Do not claim something works unless you ran it. Run `.\gradlew.bat assembleDebug` and `.\gradlew.bat testDebugUnitTest` and show the real output. If you cannot run a command, say so.
17. Never log or display real personal data. All names in the app are the sample data only.

## Finishing
18. When done: list the files changed, list the exact commands to verify, then STOP.
19. Never kill processes, delete files, or change anything outside C:\rovai-front without asking me first. Never start the emulator unless I ask.
