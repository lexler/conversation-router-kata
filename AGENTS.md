# AGENTS.md

Rules for AI coding agents working in this repo.

The project, its story and its domain language: @docs/project.md and `docs/terms.md`.

## Build and test

Run `./build_and_test.sh` after every change. It prints `Build and test: Ns, exit N` last. Don't pipe it into `tail` or `grep`; that hides the exit code.

`RoutingRulesAcceptanceTest` pins the routing behavior with about 15,000 input combinations in `RoutingRulesAcceptanceTest.pinAll.approved.txt`. Never edit, regenerate or re-approve that file: a refactoring that changes it changed behavior.

## Commit messages

Build every commit message with `bash tools/create-commit-message.sh <risk> <intention> "<message>"` and use its stdout. `docs/commit-notation.md` explains the codes.
