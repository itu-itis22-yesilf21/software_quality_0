# Improved Base Test Results

## Tooling
- JaCoCo: used local 	ools/jacococli.jar and 	ools/jacocoagent.jar version 0.8.12.
- JNose Test: no local jnose command or Docker runtime was available, and JNose targets Java/JUnit projects while this workspace uses plain Java main test classes. I applied JNose-guided smell checks for assertion roulette and eager-test symptoms.

## JNose-Guided Test Smell Findings
- Base tests: 30 assertion-roulette candidates because failures used grouped boolean checks and unlabeled AssertionError.
- Base tests: 30 eager-test candidates because each class aggregates several scenarios in a single main method.
- Improved tests: new coverage-gap checks use explicit failure messages through check(condition, message).

## JaCoCo Branch Coverage
- Baseline: 26/30 tasks had full branch coverage, average 97.44%.
- Improved: 29/30 tasks had full branch coverage, average 99.87%.

## Improved Tasks
- Java/6: 87.5% -> 100% branch coverage
- Java/13: 87.5% -> 100% branch coverage
- Java/19: 73.08% -> 96.15% branch coverage
- Java/46: 75% -> 100% branch coverage

## Residual Risk
- Java/19 remains at 96.15% branch coverage because JaCoCo reports one missed compiler-generated switch default branch that is unreachable through the public API.