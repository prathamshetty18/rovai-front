# Sample Data

Every screen in the frontend phase shows this data. The fake repositories must return **exactly** these values so that what you see matches this file.

**Time rules**
- `D0` = today, `D-1` = yesterday, `D-3` = 3 days ago, `D+1` = tomorrow. Dates are built relative to **now** when the app starts, in the device time zone.
- "This week" = the last 7 days (D0 back to D-6). "Last week" = the 7 days before that. "This month" = the last 30 days. "Last month" = the 30 days before that. (Simple rolling periods for the demo.)
- If a time such as "1 hour ago" would fall before 00:05 today, use 00:05 today.

---

## 1. People

**Parent (demo account)**
| Field | Value |
|---|---|
| Full name | Meera D'Souza |
| Email | parent@example.com |
| Password | Password123 |
| Mobile | +91 90000 00000 |

**Child**
| Field | Value |
|---|---|
| Name | Aarav D'Souza |
| Grade | Class 10 |
| Curriculum/board | CBSE |
| Daily learning goal | 60 minutes |
| Avatar | none (show initials "AD") |

If the parent adds a child through the Add Child screen, use the name, grade and board typed in the form on every screen. All learning data stays as the sample data below.

---

## 2. Demo inputs (fake auth and pairing)

| Where | Input | Result |
|---|---|---|
| Login | parent@example.com + Password123 | Success, go to Home |
| Login | anything else | Error "Invalid email or password" |
| Sign up | email `taken@example.com` | Error "Email already registered" |
| Sign up | any other valid email | Success, go to Add Child |
| Forgot password | any email | OTP step |
| OTP | `123456` | Verified |
| OTP | anything else | Error "Incorrect OTP" |
| Robot code | `WINI2026` | Success, "Robot connected" |
| Robot code | `EXPIRED1` | Error "This code has expired. Restart your robot to get a new one." |
| Robot code | `USED0001` | Error "This code has already been used." |
| Robot code | anything else | Error "Code not found. Check the code on your robot." |

---

## 3. Robot

| Field | Normal | Disconnected (Demo controls switch) |
|---|---|---|
| Connected | yes | no |
| Wi-Fi | connected, strong | not connected |
| Battery | 82% | 82% |
| Last active | 5 minutes ago | 3 hours ago |
| Firmware | 1.0.3 | 1.0.3 |

Add a "Robot disconnected" switch to Demo controls to show the second column.

---

## 4. Today's learning (D0)

| Field | Value |
|---|---|
| Learning time | 45 minutes |
| Daily goal | 60 minutes |
| Topics covered | 3 |
| Subjects covered | Mathematics, Science, English |
| Questions answered | 25 |
| Correct | 20 |
| Incorrect | 5 |
| Overall progress | 60% |

---

## 5. Activities

Newest first. "Q" = correct / incorrect questions. Times are local.

| ID | When | Title | Subject | Topic | Type | What the child did | Status | Min | Q |
|---|---|---|---|---|---|---|---|---|---|
| a01 | D0, 1 h ago | Mathematics: Quadratic Equations | Mathematics | Quadratic Equations | Practice | Completed 15 questions | Completed | 15 | 12 / 3 |
| a02 | D0, 2 h ago | Chemistry: Acids, Bases & Salts | Science | Acids, Bases & Salts | Study | Watched video + Notes | Completed | 15 | none |
| a03 | D0, 3 h ago | English: Grammar Practice | English | Grammar | Practice | Answered 10 questions | Completed | 15 | 8 / 2 |
| a04 | D-1, 7:45 PM | Physics: Light: Reflection & Refraction | Science | Light: Reflection & Refraction | Practice | Answered 10 questions | Completed | 20 | 8 / 2 |
| a05 | D-1, 5:10 PM | English: Reading Comprehension | English | Reading Comprehension | Reading | Read a passage and answered 5 questions | Completed | 20 | 4 / 1 |
| a06 | D-1, 4:00 PM | Social Science: Nationalism in India | Social Science | Nationalism in India | Lesson | Finished lesson 3 | Completed | 20 | none |
| a07 | D-2, 6:20 PM | Mathematics: Trigonometry Quiz | Mathematics | Trigonometry | Quiz | Quiz: 20 questions | Completed | 25 | 16 / 4 |
| a08 | D-2, 4:30 PM | Science: Life Processes | Science | Life Processes | Educational Activity | Started a plant cell activity | In progress | 30 | none |
| a09 | D-3, 5:00 PM | Social Science: Resources and Development | Social Science | Resources and Development | Reading | Read 2 pages | Completed | 20 | none |
| a10 | D-4, 6:00 PM | Science: Life Processes Quiz | Science | Life Processes | Quiz | Quiz: 20 questions | Completed | 25 | 14 / 6 |
| a11 | D-4, 3:30 PM | English: Letter Writing | English | Letter Writing | Lesson | Practised a formal letter | Completed | 15 | none |
| a12 | D-5, 6:30 PM | Mathematics: Linear Equations | Mathematics | Pair of Linear Equations | Practice | Answered 20 questions | Completed | 30 | 17 / 3 |
| a13 | D-5, 4:00 PM | Science: Electricity | Science | Electricity | Study | Watched video | Completed | 20 | none |
| a14 | D-6, 5:00 PM | Social Science: Nationalism in India Quiz | Social Science | Nationalism in India | Quiz | Quiz: 15 questions | Completed | 30 | 9 / 6 |
| a15 | D-8, 6:00 PM | English: Grammar Quiz | English | Grammar | Quiz | Quiz: 20 questions | Completed | 25 | 17 / 3 |
| a16 | D-11, 5:30 PM | Science: Chemical Reactions Quiz | Science | Chemical Reactions | Quiz | Quiz: 20 questions | Completed | 25 | 12 / 8 |
| a17 | D-13, 4:30 PM | Mathematics: Linear Equations Quiz | Mathematics | Pair of Linear Equations | Quiz | Quiz: 20 questions | Completed | 25 | 18 / 2 |

**Filters**
- Today: a01 to a03. This Week: a01 to a14. This Month and All Time: all 17.
- Example of a natural empty result: Social Science + Today.
- Home "Recent activity": a01, a02, a03.

---

## 6. Learning time per day (minutes, oldest to newest)

| Period | Per-day minutes | Total | Daily goal |
|---|---|---|---|
| This week (D-6 to D0) | 30, 50, 40, 20, 55, 60, 45 | 300 | 60 |
| Last week | 25, 40, 35, 30, 0, 45, 40 | 215 | 60 |
| This month | 30 days: first 20 days 39, last 10 days 40 | 1180 | 60 |
| Last month | 30 days: first 10 days 34, remaining 20 days 35 | 1040 | 60 |

---

## 7. Overall progress

| Period | Percent | Status label | Completed activities | In progress | Per-day percent (oldest to newest) |
|---|---|---|---|---|---|
| This week | 60 | Keep going | 13 | 1 | 55, 56, 57, 58, 58, 59, 60 |
| Last week | 55 | Keep going | 11 | 1 | 50, 51, 52, 53, 54, 54, 55 |
| This month | 60 | Keep going | 38 | 3 | 30 values rising evenly from 48 to 60 |
| Last month | 48 | Keep going | 31 | 2 | 30 values rising evenly from 40 to 48 |

Status labels: 70% or more "Good progress", 40 to 69% "Keep going", below 40% "Needs attention".

---

## 8. Subject progress

| Subject | Percent | Completed topics | Total topics | Needs more practice |
|---|---|---|---|---|
| Mathematics | 70 | 7 | 10 | 1 |
| Science | 60 | 6 | 10 | 2 |
| Social Science | 50 | 5 | 10 | 2 |
| English | 60 | 6 | 10 | 1 |

Overall progress = completed topics / all topics = 24 / 40 = 60%.

Subject progress is the same for every period in the demo.

---

## 9. Topics mastery

Status rule: 80% or more Completed; below 50% Needs practice; otherwise Learning.

**Mathematics**
| Topic | % | Status |
|---|---|---|
| Real Numbers | 92 | Completed |
| Polynomials | 88 | Completed |
| Pair of Linear Equations | 90 | Completed |
| Quadratic Equations | 85 | Completed |
| Arithmetic Progressions | 83 | Completed |
| Triangles | 81 | Completed |
| Coordinate Geometry | 80 | Completed |
| Trigonometry | 72 | Learning |
| Circles | 65 | Learning |
| Statistics | 42 | Needs practice |

**Science**
| Topic | % | Status |
|---|---|---|
| Chemical Reactions | 86 | Completed |
| Acids, Bases & Salts | 84 | Completed |
| Metals and Non-metals | 82 | Completed |
| Life Processes | 80 | Completed |
| Control and Coordination | 81 | Completed |
| Heredity | 80 | Completed |
| Light: Reflection & Refraction | 68 | Learning |
| Electricity | 60 | Learning |
| Carbon and its Compounds | 45 | Needs practice |
| Magnetic Effects of Electric Current | 40 | Needs practice |

**Social Science**
| Topic | % | Status |
|---|---|---|
| Rise of Nationalism in Europe | 85 | Completed |
| Nationalism in India | 82 | Completed |
| Resources and Development | 84 | Completed |
| Agriculture | 80 | Completed |
| Water Resources | 81 | Completed |
| Minerals and Energy | 62 | Learning |
| Print Culture | 58 | Learning |
| Power Sharing | 55 | Learning |
| Federalism | 45 | Needs practice |
| Money and Credit | 40 | Needs practice |

**English**
| Topic | % | Status |
|---|---|---|
| Letter Writing | 88 | Completed |
| Reading Comprehension | 85 | Completed |
| Grammar | 82 | Completed |
| Notice Writing | 84 | Completed |
| Formal Email | 81 | Completed |
| Dialogue Writing | 80 | Completed |
| Story Writing | 66 | Learning |
| Report Writing | 60 | Learning |
| Prose Comprehension | 55 | Learning |
| Poetry Analysis | 47 | Needs practice |

---

## 10. Tests and quizzes

| ID | Name | Subject | Date | Score | Total | Correct | Incorrect | Percentage |
|---|---|---|---|---|---|---|---|---|
| t1 | Trigonometry Quiz | Mathematics | D-2 | 16 | 20 | 16 | 4 | 80% |
| t2 | Life Processes Quiz | Science | D-4 | 14 | 20 | 14 | 6 | 70% |
| t3 | Nationalism in India Quiz | Social Science | D-6 | 9 | 15 | 9 | 6 | 60% |
| t4 | Grammar Quiz | English | D-8 | 17 | 20 | 17 | 3 | 85% |
| t5 | Chemical Reactions Quiz | Science | D-11 | 12 | 20 | 12 | 8 | 60% |
| t6 | Linear Equations Quiz | Mathematics | D-13 | 18 | 20 | 18 | 2 | 90% |
| t7 | Quadratic Equations Quiz | Mathematics | D-35 | 15 | 20 | 15 | 5 | 75% |
| t8 | Light Quiz | Science | D-40 | 13 | 20 | 13 | 7 | 65% |

- This week: t1, t2, t3. Last week: t4, t5, t6. This month: t1 to t6. Last month: t7, t8.
- Average: this week 70%, last week 78%.

---

## 11. Comparisons

**Week periods (this week vs last week)**
| Row | Current | Previous | Change |
|---|---|---|---|
| Learning time | 300 min | 215 min | up 85 min |
| Overall progress | 60% | 55% | up 5 points |
| Test average | 70% | 78% | down 8 points |
| Mathematics | 70% | 66% | up 4 |
| Science | 60% | 56% | up 4 |
| Social Science | 50% | 50% | no change |
| English | 60% | 48% | up 12 |

**Month periods (this month vs last month)**
| Row | Current | Previous | Change |
|---|---|---|---|
| Learning time | 1180 min | 1040 min | up 140 min |
| Overall progress | 60% | 48% | up 12 points |
| Test average | 74% | 70% | up 4 points |
| Mathematics | 70% | 60% | up 10 |
| Science | 60% | 50% | up 10 |
| Social Science | 50% | 44% | up 6 |
| English | 60% | 38% | up 22 |

**Last week (compared with the week before)**
| Row | Current | Previous | Change |
|---|---|---|---|
| Learning time | 215 min | 190 min | up 25 min |
| Overall progress | 55% | 53% | up 2 points |
| Test average | 78% | 72% | up 6 points |
| Mathematics | 66% | 63% | up 3 |
| Science | 56% | 54% | up 2 |
| Social Science | 50% | 49% | up 1 |
| English | 48% | 46% | up 2 |

**Last month (compared with the month before)**
| Row | Current | Previous | Change |
|---|---|---|---|
| Learning time | 1040 min | 900 min | up 140 min |
| Overall progress | 48% | 40% | up 8 points |
| Test average | 70% | 66% | up 4 points |
| Mathematics | 60% | 52% | up 8 |
| Science | 50% | 42% | up 8 |
| Social Science | 44% | 38% | up 6 |
| English | 38% | 28% | up 10 |

---

## 12. Credits

Total credits: **140**. Newest first. These are sample values only, not the real rules.

| ID | When | Amount | Reason | Activity | Achievement/reward |
|---|---|---|---|---|---|
| c1 | D-1 | 10 | Great Effort | Mathematics: Trigonometry Quiz | none |
| c2 | D-2 | 15 | Consistent Learner | none | 5-day learning streak |
| c3 | D-3 | 20 | Topic Master | Mathematics: Quadratic Equations | Topic Master: Quadratic Equations |
| c4 | D-5 | 10 | Great Effort | Mathematics: Linear Equations | none |
| c5 | D-6 | 10 | Great Effort | Social Science: Nationalism in India Quiz | none |
| c6 | D-8 | 20 | Topic Master | English: Grammar Quiz | Topic Master: Grammar |
| c7 | D-9 | 10 | Great Effort | none | none |
| c8 | D-11 | 15 | Consistent Learner | none | 3-day learning streak |
| c9 | D-12 | 10 | Great Effort | none | none |
| c10 | D-14 | 20 | Topic Master | Science: Chemical Reactions Quiz | Topic Master: Chemical Reactions |

Motivation message on the Progress hub: "Great job! Aarav is on a 7-day streak."

---

## 13. Achievements

Newest first. Home shows the latest 3; the Achievements screen shows all 5.

| ID | Kind | Title | Description | When |
|---|---|---|---|---|
| ac1 | Streak | 7-day learning streak | Learned every day this week | D0 |
| ac2 | Subject achievement | Topic Master: Quadratic Equations | Mastered a Mathematics topic | D-3 |
| ac3 | Consistent learning | Consistent Learner | Learned 5 days in a row | D-2 |
| ac4 | Milestone | 100 questions answered | Answered 100 practice questions | D-9 |
| ac5 | Streak | 3-day learning streak | Learned 3 days in a row | D-12 |

---

## 14. Upcoming goals

| ID | Title | Subject | Topic | Date | Time |
|---|---|---|---|---|---|
| g1 | Practice Algebra | Mathematics | Pair of Linear Equations | D+1 | 5:00 PM |
| g2 | Revise Acids, Bases & Salts | Science | Acids, Bases & Salts | D+2 | 4:30 PM |
| g3 | Read Chapter 4 | English | Reading Comprehension | D+3 | 6:00 PM |

Goals added in the app are kept in memory and appear on Home and the Goals screen.

---

## 15. Notifications

3 unread. Newest first.

| ID | When | Type | Title | Message | Read |
|---|---|---|---|---|---|
| n1 | 2 hours ago | Achievement | New achievement | Aarav reached a 7-day learning streak. | no |
| n2 | 5 hours ago | Learning update | Today's learning | Aarav completed 3 activities today. | no |
| n3 | D-1 | Robot update | Robot update | Wini software 1.0.3 was installed. | no |
| n4 | D-2 | Important | Welcome to ROAVAI | Your child's learning progress will appear here. | yes |
| n5 | D-3 | Achievement | Topic Master | Aarav mastered Quadratic Equations. | yes |

---

## 16. Home motivational message

Fixed text: "Keep going, Aarav! You are doing great today." (Use the child's name from the profile.)
