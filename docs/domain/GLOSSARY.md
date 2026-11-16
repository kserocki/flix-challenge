# Glossary

Use these terms exactly, in code and in docs.

| Term | Meaning | In code |
|---|---|---|
| **Station** | A Flix stop where buses depart. Has an id (UUID), a name and an IANA time zone. | `Station(id, name, timeZone)` |
| **Station time zone** | The IANA zone of the station (for example `Europe/Berlin`). Every departure time is shown in this zone. | `Station.timeZone: TimeZone` |
| **Ride** | One bus trip on a line. The API returns the rides that call at the station within the window. | API: `rides[]` |
| **Departure** | Our domain view of a ride at this station: line code, direction, scheduled departure. | `Departure` |
| **Line code** | Short public identifier of a line, for example `N160`, `L027`, `1322`. It isn't always a number. | `Departure.lineCode: String` |
| **Direction** | The ride's final destination as shown to passengers, for example `Trieste (Autostazione)`. | `Departure.direction: String` |
| **Scheduled departure** | The planned departure instant from this station, in UTC. | `Departure.scheduledAt: Instant` |
| **Timetable window** | The time range we ask for: `[now, now + 2h]`. | `TimetableWindow(from, to)` |
| **Station timetable** | A station plus its departures in the window, sorted by scheduled time. | `StationTimetable(station, departures)` |
| **Deviation** | Real-time delay info (`LATE`, `ON_TIME`, estimated time). **Not used yet** (out of scope). | – |
| **Brand** | The operator brand (`FlixBus`, `FlixTrain`, …). Not shown yet. | – |
