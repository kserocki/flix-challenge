# The challenge brief (Flix Android coding challenge)

Summary of the original PDF. The PDF is the source; this file is the copy agents read.

## Specification
After starting the app, the user is presented with a **timetable of one Flix station** with bus departures.

### Acceptance criteria
- Each timetable item contains the **line code**, **direction** and **departure time**.
- Departure time is displayed in the **time zone of the station**.

**"We expect you to write the code you would consider production-ready."**

## Additional info from the brief
- Kotlin. Any third-party library may be used.
- Clean, testable and maintainable code is valued.
- **UI and handling of edge cases matter.**
- Using git with **small commits** is a big plus.
- Not timed.

## Reference screen (from the brief)
A green top app bar, then a plain list. Each row has the direction (primary text) with the line code below it (secondary text), and the departure time `HH:mm` on the right. For example: `Essen / L027 / 12:30`.

## API
See `API.md`.

## Scope agreed with the user (on top of the brief)
- Loading, empty, and error states with retry. Pull-to-refresh. Day headers when the window crosses midnight.
- A station header showing the station name and its time zone.
- Out of scope for now: offline cache, auto-refresh (#11), real-time delays (`status.deviation`, #10), an iOS app.
