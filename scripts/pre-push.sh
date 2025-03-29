#!/bin/bash

# Run spotlessCheck
echo "Running spotless..."
./gradlew --init-script gradle/init.gradle.kts spotlessApply --no-configuration-cache
if [ $? -ne 0 ]; then
  echo "Spotless check failed. Please run below command:"
  echo "./gradlew --init-script gradle/init.gradle.kts spotlessApply --no-configuration-cache"
  exit 1
fi

# Run lint
echo "Running lint..."
./gradlew lint --quiet
if [ $? -ne 0 ]; then
  echo "Lint failed. Please fix lint issues."
  exit 1
fi

# Run unit tests
echo "Running unit tests..."
./gradlew testDebugUnitTest --quiet
if [ $? -ne 0 ]; then
  echo "Unit tests failed. Fix them before pushing."
  exit 1
fi

exit 0