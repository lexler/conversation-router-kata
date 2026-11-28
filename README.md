# Conversation router kata

FalconAir recently bought DuckWings. Customers of both airlines write to the airlines' pages on Facebook and Instagram, and one bot answers on all of them. Until the loyalty programs merge, each airline keeps its own support teams, so for every conversation the conversation router decides which team's queue it goes to.

The routing rules grew one exception at a time: a team per airline, per market and per language, VIP customers, executive mentions, disruptions. Today they all live in one method, `RoutingRules.getAssignedWorkQueue`, and the developers are afraid to touch it.

The kata: refactor that method into code the team can change again, without changing what it does. Use any technique, by hand or with AI.

## Safety net

`RoutingRulesAcceptanceTest` pins today's behavior: about 15,000 combinations of channel, page, brand, verification and tags, recorded in `RoutingRulesAcceptanceTest.pinAll.approved.txt`. A refactoring that changes that file changed behavior.

## Run

1. Install [mise](https://mise.jdx.dev/getting-started.html). It provides the right Java version.
2. Run `./build_and_test.sh`, or `build_and_test.bat` on Windows. The first run downloads Java 25 and Maven.

It ends with `Build and test: Ns, exit 0` when all tests pass.

## More

- `docs/project.md`: the story in full
- `docs/terms.md`: the domain language
- `docs/habit-hooks.md`: install and set up Habit Hooks smell sensors, and `docs/processes/habit-hooks.process.md`: the cleanup loop for an AI agent