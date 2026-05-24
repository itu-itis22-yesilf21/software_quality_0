# Software Quality Project — ITU BLG 475E (Spring 2025–2026)

Combined Phase 1 + Phase 2 deliverables for the BLG 475E Software
Quality and Testing course project.

- **Phase 1** — code generation, base testing, JaCoCo branch coverage,
  JNose-guided test-smell inspection, and ECP/BVA-driven mutation
  testing applied to 30 selected HumanEval-Java tasks.
- **Phase 2** — integration testing of a multi-method `BookScan` class
  that composes the three HumanEval helpers `howManyTimes` (Java/18),
  `strlen` (Java/23), and `flipCase` (Java/27). Two LLMs × two prompt
  variants (unmodified+combined vs. edited+combined) = four
  `BookScan` implementations, each with its own integration suite,
  coverage report, smell-metric file, and mutation suite.

The combined final report lives in [`report/`](report/). The repository
is the **artefact citation target**; the report's Conclusion section
links here.

## Models

| Label | Model | Phase 1 directory | Phase 2 directory |
|---|---|---|---|
| LLM A | **GPT-5.5**         | `generated_java/Java_X/`               | `bookscan/llm_a/{unmodified,edited}/` |
| LLM B | **Gemini 3.1 Pro**  | `gemini_process/generated_code/Java_X/`| `bookscan/llm_b/{unmodified,edited}/` |

Each LLM was accessed through its hosted chat UI with default
sampling. Every prompt + response pair is logged.

## Directory layout

```
README.md                       this file
phase2.md                       Phase 2 14-step plan and per-step outcomes
2526_BLG_475E_Project.pdf       course brief (untracked; informational)
report/
  report.tex                    combined Phase 1 + Phase 2 report
  references.bib                bibliography (5 papers)
  figures/                      Phase 2 PNG charts (Step 11)
  README.md                     report-specific compile + placeholder audit
generated_java/Java_X/          Phase 1 GPT-5.5 outputs
  Solution.java, BaseTest.java, ImprovedBaseTest.java, BlackBoxTest.java
gemini_process/                 Phase 1 Gemini 3.1 Pro outputs + Phase 1 logs
  generated_code/Java_X/Solution.java + SolutionTest.java
  logs/code_generation_log.md
  junit-platform-console-standalone-1.9.3.jar  (also used in Phase 2)
bookscan/                       Phase 2 workspace
  llm_a/{unmodified,edited}/    GPT-5.5 BookScan variants + tests + smoke
  llm_b/{unmodified,edited}/    Gemini  BookScan variants + tests + smoke
  logs/                         every Phase 2 LLM interaction
  reports/                      machine + human analysis outputs
  coverage_reports/             per-variant JaCoCo CSV / XML / JSON
  run_smoke.py                  Step 4 driver
  run_integration.py            Step 6 driver
  run_coverage.py               Step 7 driver
  run_black_box.py              Step 9 driver
  analyze_test_smells.py        Step 8 metric extractor
  make_comparison.py            Step 11 aggregator + chart generator
tools/                          JaCoCo 0.8.12 binaries (agent + CLI)
coverage_reports/               Phase 1 coverage outputs
```

## Prerequisites

- **Java 11 source compatibility.** We tested on `javac` / `java`
  21.0.10; any JDK ≥ 11 will work.
- **Python 3.8+** with `matplotlib` for the Step 11 chart generator;
  every other Phase 2 orchestrator uses only the standard library.
- No build system. The project is a flat collection of `.java` files;
  each variant directory compiles and runs in place.

## Running Phase 1 end-to-end

Phase 1 was already complete on the `gemini` branch when Phase 2
started. Re-running it is unusual; the typical flow is to read the
artefacts:

```
git switch gemini                            # Phase 1 final state
cat improved_test_results.md                 # smell + coverage summary
cat black_box_equivalence_analysis.md        # ECP/BVA tables for 30 tasks
cat base_test_effectiveness_assessment.md    # per-task effectiveness scoring
cat coverage_reports/improved_branch_coverage_summary.json
```

Per-task source + tests for a single task (e.g. `Java/8`):

```
cd generated_java/Java_8
javac -encoding UTF-8 Solution.java BaseTest.java ImprovedBaseTest.java BlackBoxTest.java
java -cp . BaseTest && java -cp . ImprovedBaseTest && java -cp . BlackBoxTest
```

To regenerate Phase 1 JaCoCo coverage:

```
java -javaagent:tools/jacocoagent.jar=destfile=out.exec,includes=Solution* \
     -cp generated_java/Java_8 BaseTest
java -jar tools/jacococli.jar report out.exec \
     --classfiles generated_java/Java_8/Solution.class \
     --csv coverage_reports/base/Java_8.csv
```

## Running Phase 2 end-to-end

From the repository root, on the `phase2` branch:

```
git switch phase2

python bookscan/run_smoke.py         # Step 4 — compile + smoke run
python bookscan/run_integration.py   # Step 6 — integration tests
python bookscan/run_coverage.py      # Step 7 — JaCoCo coverage
python bookscan/analyze_test_smells.py > bookscan/reports/test_smell_metrics.json  # Step 8
python bookscan/run_black_box.py     # Step 9 — mutation suites
python bookscan/make_comparison.py   # Step 11 — comparison + charts
```

Each script writes deterministic outputs into `bookscan/reports/` and
`bookscan/coverage_reports/`. Class files are gitignored. The full
sequence completes in well under a minute on a laptop.

## Reproducing the Phase 2 LLM-generated artefacts

If you want to regenerate the four `BookScan.java` files or the four
`BookScanIntegrationTest.java` files from scratch, the prompt
templates are in:

- `bookscan/logs/prompt_design_log.md` — Variant 1 + Variant 2
  prompts for the BookScan class generation step.
- `bookscan/logs/integration_test_prompt_design.md` — shared template
  for the integration-test generation step.

Paste each prompt verbatim into the relevant LLM UI as a single turn.
The full prompt/response/use-note for every original run is recorded
in `bookscan/logs/class_generation_log.md` and
`bookscan/logs/integration_test_generation_log.md` for byte-for-byte
verification.

## Branch policy

| Branch | Contains |
|---|---|
| `main`     | pre-project baseline (untouched during the project) |
| `gemini`   | Phase 1 final snapshot |
| `phase2`   | Phase 1 + all Phase 2 work (this is the submission branch) |

Phase 2 commits all carry the `Phase 2 / Step N: <what & why>` message
prefix; per-run commits use the `Phase 2 / Step N.M` form (e.g.,
`Step 3.2` for the second of the four BookScan generation runs). The
commit history tells the story of the development process on its own,
per the project rules.

## Reports and writeups (where to look first)

| Question | Document |
|---|---|
| "Where is the combined report?" | `report/report.tex` (compile per `report/README.md`). |
| "How does the LLM comparison look?" | `bookscan/reports/comparison.md` + the two PNGs in `report/figures/`. |
| "What integration failures were observed and why?" | `bookscan/reports/failure_analysis.md`. |
| "What were the JaCoCo numbers?" | `bookscan/coverage_reports/summary.md` for Phase 2; `coverage_reports/improved_branch_coverage_summary.json` for Phase 1. |
| "What does each test smell look like?" | `bookscan/reports/test_smell_findings.md`. |
| "What ECP classes were used?" | `bookscan/reports/ecp_bva_table.md` for Phase 2; `black_box_equivalence_analysis.md` for Phase 1. |
| "What LLM prompts were used, verbatim?" | `bookscan/logs/*.md` for Phase 2; `gemini_process/logs/code_generation_log.md` for Phase 1. |
| "How was Phase 2 planned?" | `phase2.md` — the 14-step plan plus per-step outcomes. |

## What to submit to Ninova

The Ninova upload is the **compiled PDF only**. The code, prompts,
logs, and analyses live here on GitHub; the report's Conclusion
section cites this repository directly. Do not bundle the source
tree into the Ninova upload.
