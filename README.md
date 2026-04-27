# Software Quality Project

This repository contains a software quality and testing study for 30 selected
HumanEval-style Java programming tasks. For each selected task, the project
includes a generated Java solution, original dataset tests, improved coverage
tests, and black-box mutation tests based on equivalence partitioning and
boundary-value analysis.

GPT-5.5 and Gemini 3.1 Pro were used during the solution generation, test
generation, and analysis workflow.

The project is organized as a collection of plain Java files and analysis
artifacts. It is not a Maven, Gradle, or npm project.

## Scope

The selected task IDs are listed in `prompts.txt`:

```text
0, 1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 13, 14, 15, 17, 19, 20, 23, 25, 28, 32, 33, 36, 38, 39, 40, 44, 46, 49, 50
```

Each task has a folder named `Java_<id>` containing:

- `Solution.java`: generated solution for the task.
- `BaseTest.java`: original dataset tests converted into a plain Java test class.
- `ImprovedBaseTest.java`: additional tests targeting branch coverage gaps.
- `BlackBoxTest.java`: equivalence-class and boundary-value mutation tests.

## Repository Layout

```text
.
|-- generated_java/
|   `-- Java_<id>/
|       |-- Solution.java
|       |-- BaseTest.java
|       |-- ImprovedBaseTest.java
|       `-- BlackBoxTest.java
|-- coverage_reports/
|   |-- base/
|   |-- improved/
|   |-- base_branch_coverage.json
|   |-- base_branch_coverage_summary.json
|   |-- improved_branch_coverage.json
|   `-- improved_branch_coverage_summary.json
|-- gemini_process/
|   |-- generated_code/
|   |-- coverage_reports/
|   |-- logs/
|   |-- reports/
|   `-- rewrite.py
|-- base_tests.json
|-- base_test_results.json
|-- improved_test_results.json
|-- improved_test_results.md
|-- selected_task_specs.json
|-- black_box_equivalence_analysis.md
|-- black_box_test_results.json
|-- base_test_effectiveness_assessment.md
|-- generated_solutions.jsonl
|-- prompts.txt
`-- CHAT_LOG.md
```

## Requirements

- JDK 11 or newer
- Optional: Python, only for one-off scripts such as `gemini_process/rewrite.py`
- Optional: JaCoCo 0.8.12 jars for regenerating branch coverage reports

The repository does not include a root build file. Compile and run tests from an
individual task directory.

## Running A Task

Example using `generated_java/Java_8`:

```powershell
cd generated_java\Java_8
javac Solution.java BaseTest.java ImprovedBaseTest.java BlackBoxTest.java
java BaseTest
java ImprovedBaseTest
java BlackBoxTest
```

`BlackBoxTest` calls `BaseTest.main(args)` before running its additional
black-box mutation checks, so it exercises both the original tests and the
extra boundary/equivalence tests.

## Main Results

- Generated solutions passed the original dataset tests: 30/30.
- `BaseTest.java` suites compiled and passed: 30/30.
- Baseline branch coverage: 26/30 tasks had full branch coverage, with an
  average of 97.44%.
- Improved branch coverage: 29/30 tasks had full branch coverage, with an
  average of 99.87%.
- Black-box mutation suites compiled and passed: 30/30.
- Base test effectiveness assessment:
  - High: 3/30
  - Medium: 17/30
  - Low: 10/30

See `CHAT_LOG.md` for the full chronological record of work and verification.

## Analysis Artifacts

- `improved_test_results.md` summarizes JaCoCo branch coverage improvements and
  JNose-guided test smell observations.
- `black_box_equivalence_analysis.md` lists valid equivalence classes,
  out-of-contract invalid classes, boundary conditions, and added mutation
  tests for each task.
- `base_test_effectiveness_assessment.md` evaluates how well the original base
  tests cover the identified black-box classes and boundaries.
- `coverage_reports/` contains branch coverage JSON summaries and per-task CSV
  reports for base and improved tests.

## Gemini Process Copy

`gemini_process/` contains a parallel set of generated code, reports, logs, and
helper scripts from an alternate generation/evaluation workflow. Its structure
mirrors the primary `generated_java/` and `coverage_reports/` outputs, but the
root-level artifacts should be treated as the main project deliverables. This
workflow includes artifacts produced with Gemini 3.1 Pro.

## Notes

- The test classes use plain Java `main` methods and custom `check` helpers
  instead of JUnit.
- Invalid inputs are documented as out-of-contract when the original prompt does
  not define expected behavior.
- `gemini_process/rewrite.py` contains local path assumptions and may need
  editing before it can be reused on another machine.
