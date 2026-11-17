# ADR-0011: Trailblaze for agent-driven UI testing and replayable trails

- Status: Accepted · 2026-11-16

## Context
The `qa` agent used to drive the emulator with raw `adb` taps on guessed coordinates and screenshots. That's slow, brittle, and leaves nothing reusable behind. The user asked us to use [Trailblaze](https://github.com/block/trailblaze) (Block, Apache 2.0).

## Decision
- `qa` drives the app through the **Trailblaze CLI**: `trailblaze snapshot` (a native accessibility tree with element refs) and `trailblaze tool …` with `-s "<intent>"` on every step.
- Each passing scenario is saved as a trail under `trails/<scenario>/trail.yaml` and committed. Replay (`trailblaze run trails/<scenario>`) is deterministic and doesn't use an LLM.
- `adb` stays for **device settings** that Trailblaze doesn't own: airplane mode, device time zone, dark mode, font scale.
- The data is live (a real API), so trails assert **structure** (the station header, the `HH:mm` format, a list or the empty state is shown), never specific lines or times.
- Version pinned: **2026.11.12**, installed with `install.sh` to `~/.trailblaze` (`TRAILBLAZE_VERSION=2026.11.12`). The agent skill is vendored in `.claude/skills/trailblaze/`. Run `trailblaze skill status` after upgrading.
- The built-in self-heal agent (which needs its own LLM key) isn't used. Claude Code is the driving agent.
- Trails aren't run in CI yet (an emulator on GitHub Actions is slow and flaky). Tracked in #6.

## Alternatives
- Raw `adb` + screenshots: no reusable artefact, coordinate guessing.
- Maestro: similar YAML flows, but authoring isn't agent-native and the tests are written by hand.
- Compose UI tests (`createComposeRule`): complementary, for component-level checks. The JVM semantics tests run with Robolectric today (`TimetableContentSemanticsTest`); full state coverage is #12. Trailblaze covers the end-to-end app on a device.

## Consequences
- + Manual QA becomes regression tests as a by-product. Re-testing after a review round is one command.
- + Per-step screenshots, the hierarchy and video for the reviewer and the PR.
- − A young tool with weekly releases. Pinned and recorded here. Upgrades are deliberate.
