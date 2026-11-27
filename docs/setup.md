# Setup

## Build and test

1. Install Java 25. With [mise](https://mise.jdx.dev/getting-started.html), run `mise install` in the repo; it reads `mise.toml`. Any other JDK 25 on the `PATH` works too.
1. Run `./build_and_test.sh`, or `build_and_test.bat` on Windows. Maven comes with the wrapper, so nothing else is needed.
1. Done when it ends with `Build and test: Ns, exit 0`.

## Commit messages

Commits follow Arlo's commit notation, `<risk> <intention> <description>`, described in `docs/commit-notation.md`. `tools/create-commit-message.sh` builds the message from names, so nobody has to remember the codes:

```
bash tools/create-commit-message.sh proven refactor "extract method"
- r extract method
```

## Habit Hooks

The `habit-hooks` branch adds code-smell sensors. Its `docs/habit-hooks.md` has the install steps.
