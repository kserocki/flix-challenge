---
name: implement
description: Flix Timetable team workflow for implementing GitHub issues (#N) with the agent team in .claude/agents. planner plans (user approves), core-dev and ui-dev build work packages in parallel, consultant helps, qa tests on the emulator with screenshots, reviewer reviews (loops back on changes), then a PR is opened and the PO (orchestrator) updates domain docs. Use whenever the user asks to implement or work on one or more issues (e.g. "/implement #3", "zrób #3").
---

# Implement GitHub issues with the agent team

## Rule zero: never guess
This applies to everyone, including you (the orchestrator). If something is unclear, ask. Product questions come to you as the **PO**. You answer them from `docs/domain/`, or ask the user (AskUserQuestion with options). Never invent an answer on behalf of an agent or the user.

## Roles
| Agent (`subagent_type`) | Model | Role |
|---|---|---|
| `planner` | Opus | plan + work packages, answers plan questions |
| `core-dev` | Sonnet | KMP core, ViewModels, use cases, build, CI |
| `ui-dev` | Sonnet | Compose UI, designsystem, app shell |
| `consultant` | Opus | technical advice for stuck implementers |
| `qa` | Sonnet | emulator tests + screenshots |
| `reviewer` | Opus | review → APPROVED / CHANGES_REQUESTED |

**You (orchestrator + PO):** spawn agents, route questions, keep the work folder, talk to the user, own `docs/domain/`, and own git pushes, GitHub issues and PRs.

## Question routing
An agent may end its turn with a `QUESTION` block (`to: consultant | planner | PO`).
1. Append it to `.claude/work/<N>/questions.md` (from, to, question).
2. `consultant` / `planner`: spawn it with the question and pointers, or reuse a running one via SendMessage. `PO`: answer from `docs/domain/`, or ask the user.
3. Resume the asker (SendMessage) with the answer, and log the answer.
4. If the answer is new product knowledge, add it to `docs/domain/` in the same branch.

## Work folder and worktree
- Worktree: `git worktree add ../FlixChallenge-<N> -b <type>/<N>-<slug> origin/main`. Every agent works **only** there (give them the absolute path).
- Work folder (git-ignored, in the main checkout): `.claude/work/<N>/` with `issue.md`, `plan.md`, `questions.md`, `impl-log.md`, `test-report.md`, `media/`, `review-R.md`.

## Pipeline per issue

### 0 · Setup
`gh issue view <N> --comments` → `issue.md`. Create the worktree and branch. Add the label `in progress`.

### 1 · Plan → user approval
1. Spawn `planner` with the issue, the worktree path and the work-folder path.
2. Resolve its open questions (routing above).
3. Show the user a short summary: packages and owners, what runs in parallel, the test plan, any new ADR. Post the plan as an issue comment (`gh issue comment`).
4. **Wait for explicit approval** ("ok", "tak", "lecisz"). Changes requested → `planner` revises.

### 2 · Implementation
1. Spawn the owner of each package in the current parallel group, concurrently. Give each one the worktree path, `plan.md`, its package ids, and the latest `review-R.md` in fix rounds.
2. Route questions. When a group is done, start the next one.
3. When all packages are done, run `verify` on the whole branch (or have core-dev run it). Every gate must be green before QA.

### 3 · Manual test (`qa`)
Only for issues with user-visible changes. Spawn `qa` with the worktree and the work folder. A failing scenario doesn't go straight back to the implementers: the reviewer decides.

### 4 · Review (`reviewer`)
Spawn a fresh `reviewer` → `review-R.md`.
- `CHANGES_REQUESTED`: back to stage 2 for the listed owners only, then re-test what changed, then review R+1.
- **Max 3 rounds.** If round 3 still requests changes, stop and bring the open points to the user.

### 5 · Pull request
1. `git push -u origin <branch>` from the worktree.
2. `gh pr create --base main --title "<Summary in sentence case>"` with this body:
   ```markdown
   ## What & why
   <2–4 sentences>

   ## Highlights
   - <key change>

   ## Screenshots
   | State | Light | Dark |
   |---|---|---|

   ## How it was tested
   - Unit tests: <what>
   - Manual (qa): <scenarios, pass/fail>

   ## Review
   Approved after <R> round(s). <notable fixes>

   ## Notes
   <follow-ups as issue numbers, ADRs touched>

   Closes #<N>
   ```
   Screenshots: commit the selected ones under `docs/screenshots/` in the branch (the repo is private, so external hosting isn't used) and link them relatively.
3. Wait for CI (`gh pr checks <pr> --watch` with a Bash timeout). If it's red, send it back to the owner.
4. Create follow-up issues found in the review (`gh issue create`).
5. **The user merges** (squash). Never merge or push to `main` yourself unless the user explicitly says so. After the merge: `git worktree remove`, and delete the local branch.

## Report to the user (per issue)
PR link · owners involved · review rounds · qa pass/fail · questions asked and answered · follow-ups.
