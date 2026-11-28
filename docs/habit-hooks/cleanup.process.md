# Habit Hooks sweep

Habit Hooks is a code-smell sensor tool, run as `habit-hooks`. It reports structural smells (long methods, complexity, deep nesting, too many parameters, oversized files, duplication) with coaching guidance. Follow that guidance and clean up the code. Setup: `docs/habit-hooks.md`.

The target is `src/main/java/com/lexler/kata/routingrules/RoutingRules.java`. Keep refactoring it until Habit Hooks reports no findings on it and on any file the sweep created or changed. There is no limit on the number of commits.

## Before starting

1. Check `git status` that everything is checked in, otherwise stop.
1. If on `main`, create and switch to a new branch from `main` (for example `git switch -c habit-hooks-run-1 main`), so `main` stays a clean baseline.

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

If you find a potential product bug, don't fix it. Log it in `docs/habit-hooks-sweep-findings.md` and commit with `bash tools/create-commit-message.sh completely_safe documentation "<short-message>"`.

## Smells are prompts to investigate, not limits

Findings point at places worth looking at. They are not thresholds to get under. The goal is well-designed code that speaks the domain language in `docs/terms.md`: small clear methods, no duplication, cohesive classes.

- Never shuffle code between files just to reduce a line count.
- Extract a method or class only for a genuine responsibility.
- It is fine to leave a finding unresolved; say which ones and why in the report.