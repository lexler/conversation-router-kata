# Habit Hooks sweep

Habit Hooks is a code-smell sensor tool, run as `habit-hooks`. It reports structural smells (long methods, complexity, deep nesting, too many parameters, oversized files, duplication) with coaching guidance. Follow that guidance and clean up the code. Setup: `docs/habit-hooks/setup.md`.

Start with `src/main/java/com/lexler/kata/routingrules/RoutingRules.java` and clean it up in small steps until Habit Hooks reports no findings on it and on any file the sweep created or changed.

## Before starting

1. Check `git status` that everything is checked in, otherwise stop.
1. If on `main`, create and switch to a new branch from `main` (for example `git switch -c habit-hooks-run-1 main`), so `main` stays a clean baseline.

## Set up Habit Hooks

1. If the `habit-hooks` command isn't found, install it following `docs/habit-hooks/setup.md`.
1. Run `habit-hooks init`. It writes `.habit-hooks/config.toml` when there is none, leaves an existing one as it is, and lists what is still missing. Install each missing tool following `docs/habit-hooks/setup.md`, then run it again, until it ends with "Nothing missing".
1. If `git status` shows a new `.habit-hooks/config.toml`, commit it with the message from `bash tools/create-commit-message.sh completely_safe environment "set up Habit Hooks"`.

When Habit Hooks says "nothing scanned", the setup is broken, even though it still ends with "automated checks passed". Fix the setup before going on.

## Loop

1. Check `git status` that everything is checked in, otherwise stop.
1. Run `habit-hooks --branch main`, or `habit-hooks --file src/main/java/com/lexler/kata/routingrules/RoutingRules.java` before the first commit.
1. Apply one fix.
1. Run `./build_and_test.sh`. If it fails, fix or revert with `git revert`, never `git reset --hard`.
1. Commit with the message from `bash tools/create-commit-message.sh proven refactor "<short-message>"` (gives `- r <short-message>`).
1. Repeat until Habit Hooks reports no findings.

## Fixes

Don't check in with the user about any of the fixes. We have source control.

Fixes are refactorings: they must not change behavior. The acceptance test and its approved file are the judge; never edit or re-approve the approved file. Changing a method's signature is fine when you update its callers, the test included.

If you find a potential product bug, don't fix it since we're in the refactoring mode, so there should be no behavioral changes until we are done with refactoring.
Log it in `docs/habit-hooks/sweep-findings.md` and commit with `bash tools/create-commit-message.sh completely_safe documentation "<short-message>"`.

## Smells are prompts to investigate

Findings point at places worth looking at. They are not thresholds to get under. The goal is well-designed code that speaks the domain language in `docs/terms.md`: small clear methods, no duplication, cohesive classes.

- Never shuffle code between files just to reduce a line count.
- Extract a method or class only for a genuine responsibility.
- It is fine to leave a finding unresolved; say which ones and why in the report.