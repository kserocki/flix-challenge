# ADR-0010: Departure times in the station time zone, never a silent fallback

- Status: Accepted · 2026-11-16 · amended 2026-11-17

## Context
The brief requires departure times in the **station's** time zone. Travellers often look at a station's board from another zone, and showing device-local times would be wrong.

## Decision
- Parse `station.timezone` into `kotlinx.datetime.TimeZone` in the data mapper. Convert each `Instant` with that zone in presentation.
- If the zone is missing or invalid, return `DomainError.InvalidData` (TT-9), rather than falling back to UTC or to the device zone.
- Show the zone in the station header (TT-8), so the user knows which clock the times use.
- Day headers are computed in the station zone, not the device zone (TT-10).

## The station header's zone label is computed, never a platform abbreviation (amendment, #4)
A spike (`java.time.DateTimeFormatter.ofPattern("zzz", locale)`) showed that Android's ICU/CLDR data has no English short names for most zones outside the US: `Europe/Berlin` gives `CET`/`CEST` on the JVM but `GMT+01:00`/`GMT+02:00` on an Android 36 device with the default locale. A unit test of a platform-derived label would therefore pass on the host and show something else on the phone.

The label is instead computed with kotlinx-datetime alone, so it is identical on every platform and fully unit-tested:
- `<IANA id> (UTC±offset at the TT-8 instant)`, for example `Europe/Berlin (UTC+1)`, or `Europe/Berlin (UTC+2)` in summer. The offset is read with `TimeZone.offsetAt(instant)`, never cached.
- A fixed-offset zone (`TimeZone.of("+01:00")`, a `FixedOffsetTimeZone`), or a region zone whose id already equals its own offset label (`TimeZone.of("UTC")`, which kotlinx-datetime may resolve to either), shows the offset label alone (`UTC+1`, `UTC`). Checking both conditions makes the UTC case correct regardless of which `java.time.ZoneId` subtype the host JVM happens to return for it.
- Minutes are shown only when non-zero (`UTC+5:30` for `Asia/Kolkata`, `UTC+1` not `UTC+1:00` for `Europe/Berlin`).

## Consequences
- + Times are correct regardless of the device settings. Tests pin both device and station zones.
- + The zone label is deterministic and identical on the JVM and on a real device; no test diverges from what the user sees.
- − A broken zone blocks the screen instead of showing possibly wrong times. This is accepted.
- − No locale-native abbreviation (`CET`/`CEST`); the IANA id plus a numeric offset is less familiar at a glance. Accepted (Q1 of #4) and rejected, not deferred: Android's own locale data gives `GMT+01:00`/`GMT+02:00` on device, not `CET`/`CEST` (see the amendment above), so there is no reliable localised abbreviation to show instead.
