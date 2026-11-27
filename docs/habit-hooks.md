# Habit Hooks

Habit Hooks runs smell sensors over the Java code. Config: `.habit-hooks/config.toml`.
Source: `github.com/habit-hooks/habit-hooks`

## Install (once per machine)

1. Install [uv](https://docs.astral.sh/uv/getting-started/installation/), the Python tool manager Habit Hooks comes with.
1. `uv tool install --force habit-hooks --with habit-hooks-java`
   A forced install replaces every plugin, so add a `--with` for each plugin you already use.
1. Install the detectors: `brew install pmd jscpd jq`.
   On Windows, run the scripts in Git Bash or WSL, then install jscpd with `npm install -g jscpd`, jq with `winget install jqlang.jq`, and PMD from [pmd.github.io](https://pmd.github.io/) with its `bin` on the `PATH`. The Windows steps are untested.
1. Check the setup: `habit-hooks init` in the repo should end with "Nothing missing". It leaves the existing config as it is.
1. Check a run: `tools/run-habit-hooks.sh --short src/main/java/com/lexler/ae/routing/RoutingRules.java` lists the smells of the starting code, 12 findings.

## Usage

`tools/run-habit-hooks.sh` checks that the tools are installed, never reinstalls them, and runs from the repo root.

- `tools/run-habit-hooks.sh src/main/java/com/lexler/ae/routing/RoutingRules.java`: full coaching output for one file
- `tools/run-habit-hooks.sh --short src/main/java/com/lexler/ae/routing/RoutingRules.java`: compact smell summary
- `tools/run-habit-hooks.sh --branch main`: files changed since `main`, including new ones
- `tools/run-habit-hooks.sh --all`: everything under `src/main/java`

## What it checks

- pmd: more than 3 parameters, ifs nested 3 deep, cyclomatic complexity of 10 or more, methods over 12 statements, unused locals and imports, empty catch blocks
- generic: files over 200 lines, and duplication via `jscpd`, scoped to the files being checked

## Local adjustments

- `.habit-hooks/generic/sensors/` replaces the stock `jscpd` sensor, which reports the whole repo's duplication on every run, with one that keeps only clones touching the checked files.
- `.jscpd.json` scans `src/main/java` in `weak` mode with 50 tokens minimum, and ignores `import` lines so shared import blocks don't count as duplication.