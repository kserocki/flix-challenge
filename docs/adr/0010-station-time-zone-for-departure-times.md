# ADR-0010: Departure times in the station time zone, never a silent fallback

- Status: Accepted · 2026-11-16

## Context
The brief requires departure times in the **station's** time zone. Travellers often look at a station's board from another zone, and showing device-local times would be wrong.

## Decision
- Parse `station.timezone` into `kotlinx.datetime.TimeZone` in the data mapper. Convert each `Instant` with that zone in presentation.
- If the zone is missing or invalid, return `DomainError.InvalidData` (TT-9), rather than falling back to UTC or to the device zone.
- Show the zone in the station header (TT-8), so the user knows which clock the times use.
- Day headers are computed in the station zone, not the device zone (TT-10).

## Consequences
- + Times are correct regardless of the device settings. Tests pin both device and station zones.
- − A broken zone blocks the screen instead of showing possibly wrong times. This is accepted.
