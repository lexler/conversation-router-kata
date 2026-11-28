---
description: Refactor with Habit Hooks, one file at a time, each in its own sub-agent
argument-hint: <file> [more files]
disable-model-invocation: true
---

Follow `docs/habit-hooks/refactoring.process.md`.

Files to start from: $ARGUMENTS
With no file given, start from `src/main/java/com/lexler/kata/routingrules/RoutingRules.java`.

Work on one file at a time, in the order given. Give each file its own sub-agent, and pass it the process file and its starting file. Wait for it to finish before starting the next.

With more than one file, each sub-agent only fixes findings in its starting file and in the files it creates. It is done when `habit-hooks --all` shows none of those left, or only ones it decided to leave. Findings in other files belong to the other sub-agents.
