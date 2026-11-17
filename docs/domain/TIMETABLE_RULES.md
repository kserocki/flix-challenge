# Timetable rules

The behaviour the app must have. Every rule has an id, so tests and reviews can refer to it (`TT-3`). If you need a rule that isn't here, ask before inventing one.

## Window and request
- **TT-1** The window is `[now, now + 2h]`. `now` comes from the injected `Clock` at the moment of each load and each refresh. It is never cached across loads.
- **TT-2** `from`/`to` are sent as ISO-8601 UTC with millisecond precision and a `Z` suffix, for example `2026-03-05T14:11:51.485Z`.
- **TT-3** The station is fixed: Berlin central bus station, `dcbaaee6-9603-11e6-9066-549f350fcb0c`. It lives in one place (data-layer config) and is not hard-coded in the UI.

## Content and order
- **TT-4** Each row shows **direction**, **line code** and **departure time** (`HH:mm`, 24-hour).
- **TT-5** Departures are sorted by scheduled departure ascending. Ties keep API order (stable sort).
- **TT-6** Rides with the same `id` are shown once.

## Time zones
- **TT-7** Departure time is converted with `station.timezone` from the response, **never** with the device zone. A device in `America/New_York` shows Berlin departures in Berlin time.
- **TT-8** The station header shows the station name and the zone the times are in: the IANA id plus the UTC offset at the first departure's instant (or at `now` if the list is empty), for example `Europe/Berlin (UTC+1)`, or `Europe/Berlin (UTC+2)` in summer. A fixed-offset zone shows only the offset (`UTC`, `UTC+5:30`). Platform abbreviations (`CET`/`CEST`) aren't used: Android's locale data gives `GMT+01:00` for them, so they'd differ between platforms and tests.
- **TT-9** If the time zone is missing, blank or can't be resolved by kotlinx-datetime, the response counts as invalid data (`DomainError.InvalidData`). Resolvable non-region ids (`UTC`, `+01:00`) are accepted, because they still give correct times. We never fall back to UTC or to the device zone silently, because showing a wrong time is worse than showing an error.

## Days
- **TT-10** When the window crosses midnight **in the station zone**, departures are grouped under day headers (`Today`, `Tomorrow`, relative to the station-local date of `now`). If all departures fall on the same station-local day as `now`, no header is shown. A departure on any other day (not expected within the window) gets a header with its date in the device's medium date format.

## States
- **TT-11** First load shows a full-screen loading indicator.
- **TT-12** Zero departures in the window shows the station header and an empty state ("No departures in the next 2 hours") with a **Refresh** action that behaves like pull-to-refresh.
- **TT-13** An error on first load shows a full-screen error with a message that matches the error type (no connection / server problem / unexpected data) and a **Retry** button.
- **TT-14** Pull-to-refresh reloads with a new window (TT-1) and keeps the current list visible while it loads. If a refresh fails, the old list stays and a long snackbar shows the error with a **Retry** action. The list is never replaced by a full-screen error. Screen-reader users get a "Refresh departures" accessibility action, because they can't pull to refresh. A Refresh or Retry while a load is in flight is ignored (single-flight). Starting a new refresh dismisses a refresh-error snackbar that is still showing.
- **TT-15** Activity recreation (rotation, theme, font or locale change) doesn't trigger a new network call if data is already loaded (the state lives in the ViewModel). After the process is killed, the screen loads again with a fresh window, because restoring a saved list would show stale departures.

## Data tolerance
- **TT-16** A single ride that can't be shown is **skipped**, not fatal, and the rest of the list still shows. A ride can't be shown when it has no `line` or `status` object, when `id`, `line.code` or `line.direction` is missing or blank, or when `status.scheduled_timestamp` is missing or unparseable. Skipped rides are logged in debug builds.
- **TT-16a** If `rides` is non-empty but **every** ride is skipped, the response counts as `DomainError.InvalidData`, not an empty board. Showing "No departures" for broken data would mislead.
- **TT-16b** `"rides": []` is a valid empty board (TT-12). A missing or `null` `rides` key or a missing `station` is `DomainError.InvalidData`.
- **TT-17** Unknown JSON fields are ignored (the API returns many: `calls`, `location`, `vehicle`, …).
