# Habit Hooks

Habit Hooks runs smell sensors over the Java code and coaches how to fix what it finds.
Source: `github.com/habit-hooks/habit-hooks`

## Install (once per machine)

1. Install [uv](https://docs.astral.sh/uv/getting-started/installation/), the Python tool manager Habit Hooks comes with.
1. `uv tool install --force habit-hooks --with habit-hooks-java`
   A forced install replaces every plugin, so add a `--with` for each plugin you already use.
1. Install the detectors: `brew install pmd jscpd`.
   On Windows, run Habit Hooks in Git Bash or WSL, install jscpd with `npm install -g jscpd`, and PMD from [pmd.github.io](https://pmd.github.io/) with its `bin` on the `PATH`. The Windows steps are untested.

## Set up the repo

1. Run `habit-hooks init` in the repo. It detects Java, writes `.habit-hooks/config.toml`, and should end with "Nothing missing".
1. Check a run: `habit-hooks --file src/main/java/com/lexler/kata/routingrules/RoutingRules.java` lists the smells of the starting code.

## Usage

Run from the repo root.

- `habit-hooks --file src/main/java/com/lexler/kata/routingrules/RoutingRules.java`: one file
- `habit-hooks --branch main`: files changed since `main`, including new ones
- `habit-hooks --all`: the whole repo

## What it checks

- pmd: more than 3 parameters, ifs nested 3 deep, cyclomatic complexity of 10 or more, methods over 12 statements, unused locals and imports, empty catch blocks
- generic: files over 200 lines, and duplication via `jscpd`
