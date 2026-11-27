#!/bin/bash -e

started=$SECONDS
trap 'result=$?; echo "Build and test: $((SECONDS - started))s, exit $result"; exit "$result"' EXIT

mise install
mise exec -- ./mvnw --batch-mode test "$@"
