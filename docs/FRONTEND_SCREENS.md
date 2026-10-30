# ROAVAI Parent Frontend Screens & Visual Design

This document compiles the visual interfaces, screen flows, and component layouts of the **ROAVAI Parent** application built with Jetpack Compose and Material 3.

---

## 1. Home Dashboard (`NavRoutes.HOME`)
- **Key Elements:**
  - Header with greeting (`Welcome, Meera`), child name (`Aarav D'Souza`), unread notification bell with badge, and profile avatar.
  - Child Info Card (`AD` initials, Grade 10 CBSE, AI companion robot avatar, motivational quote).
  - Today's Learning summary: learning time (45 min), daily goal progress (60 min), subjects covered chips, questions answered breakdown.
  - Recent Activities, Subject Progress, Upcoming Goals, and Achievements.
  - 5-tab Material 3 bottom navigation bar.

![ROAVAI Home Screen](images/home_screen.jpg)

---

## 2. Progress Hub (`NavRoutes.PROGRESS`)
- **Key Elements:**
  - Screen Header with Progress title and subtitle description.
  - Period filter chips (`This Week`, `Last Week`, `This Month`).
  - Overall Progress card (68% On Track).
  - Learning Time card with custom daily Canvas bar chart.
  - Subject Progress cards with linear indicators (Mathematics 70%, Science 60%).
  - Credits card highlighting earned milestone rewards (+450 credits).

![ROAVAI Progress Hub](images/progress_screen.jpg)

---

## 3. Robot Setup & Pairing (`NavRoutes.ROBOT_SETUP`)
- **Key Elements:**
  - Centered robot companion icon.
  - Pairing instructions and 8-character uppercase code field (`WINI2026`).
  - Dynamic state handling: Idle, Connecting spinner, Connected success checkmark, or Error with Retry.
  - Continue action transitioning to Onboarding.
  - Small informational note regarding Bluetooth and QR pairing.

![ROAVAI Robot Setup](images/robot_setup_screen.jpg)

---

## 4. Onboarding Flow (`NavRoutes.ONBOARDING`)
- **Key Elements:**
  - 3-step pager featuring the companion mascot:
    1. **Meet ROAVAI:** *"Meet your child's AI learning companion."*
    2. **Learn, Explore & Grow:** *"Learning that feels like play."*
    3. **Stay Connected:** *"See how your child learns and grows."*
  - Dot indicators reflecting current page.
  - Next / Skip buttons on steps 1–2; "Get Started" and "Already have an account? Log in" on step 3.

![ROAVAI Onboarding](images/onboarding_screen.jpg)

---

## 5. Complete Application Route Map

| Screen / Step | Route | Primary Action / Flow |
|---|---|---|
| **Splash** | `splash` | 1.5s timer checking login status -> `robot_setup` or `home` |
| **Robot Setup** | `robot_setup` | Pairing code entry (`WINI2026`) -> `onboarding/0` |
| **Onboarding** | `onboarding/{page}` | 3 pages -> `signup` or `login` |
| **Sign Up** | `signup` | Registration validation -> `add_child` |
| **Login** | `login` | Demo credentials (`parent@example.com` / `Password123`) -> `home` |
| **Forgot Password** | `forgot/email` -> `forgot/otp` -> `forgot/new_password` | 3-step OTP (`123456`) and reset -> `login` |
| **Add Child** | `add_child` | Name, grade, board, and 4 privacy consents -> `home` |
| **Home** | `home` | Central dashboard with real-time summary |
| **Progress** | `progress` | Progress metrics, bar charts, and 7 detail screens |
| **Activity** | `activity` | Activity history with time and subject filters |
| **Robot Tab** | `robot` | Robot status, telemetry, unpair, and reconnect |
| **Profile** | `profile` | Account, consents, data management, and logout |
