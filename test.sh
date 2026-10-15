#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build/domain-tests
javac -d build/domain-tests src/main/java/dk/starswordmaster/progression/*.java src/test/java/dk/starswordmaster/progression/*.java
java -cp build/domain-tests dk.starswordmaster.progression.ProgressionTest
