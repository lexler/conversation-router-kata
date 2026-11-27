# Routing rules kata

A real-world legacy routing method, `RoutingRules.getAssignedWorkQueue`, with deeply nested conditionals. Practice lift-if and other refactorings on it, with or without AI.

The behavior is pinned by `RoutingRulesAcceptanceTest`, a combination approval test covering about 15,000 inputs.

```
./build_and_test.sh
```

Java 25 via `mise`. The story and the domain language are in `docs/`.
