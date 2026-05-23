# Phase 2 — BookScan Integration Testing: TODO List

Phase 2 is worth **35 pts** (deadline **2026-05-25**) and the final report
must merge Phase 1 + Phase 2 and be **≥ 8 pages**. The same project rules
apply: both LLMs are used, all interactions are logged, commit messages are
step-tagged ("Phase 2 / Step N: …"), and source files carry the
`@Authors / Student Names / Student IDs` header.

The three HumanEval methods that must live inside `BookScan` are:

| HumanEval ID | Method (HumanEval name) | Purpose in BookScan |
|---|---|---|
| Java/18 `howManyTimes` | count occurrences of a substring (overlapping) | substring counting per line |
| Java/23 `strlen` | length of a string | computing word length |
| Java/27 `flipCase` | flip case of every letter | normalising case before matching |

The required behaviour is: *given a text and a target word length, return how
many times each word of that length appears and in which line(s) it appears.*

---

## Step 1 — Phase-2 setup
- [x] Branch off `gemini` into `phase2` and commit `phase2.md` as the planning artefact.
- [x] Create folder layout for Phase-2 artefacts:
  - `bookscan/llm_a/{unmodified,edited}/` and `bookscan/llm_b/{unmodified,edited}/` for the two LLMs × two prompt variants
  - `bookscan/logs/` for per-step prompt/response logs (mirroring `gemini_process/logs/`)
  - `bookscan/coverage_reports/` for JaCoCo output
  - `bookscan/reports/` for raw test run output
- [x] Record which LLM is "LLM A" and which is "LLM B" — confirmed from the project `README.md` on `main`:
  - **LLM A = GPT-5.5** (Phase 1 output in `generated_java/`)
  - **LLM B = Gemini 3.1 Pro** (Phase 1 output in `gemini_process/generated_code/`)
  - These must be used verbatim in the final report; replace the remaining `LLM A` placeholder in `report/report.tex` with `GPT-5.5`.

## Step 2 — Prompt engineering for `BookScan`
- [x] Drafted the **unmodified & combined prompt** (Variant 1): concatenates the three HumanEval prompts (#18, #23, #27) verbatim and adds a single-sentence BookScan responsibility paragraph. Tokenisation, case rule, output type, and edge cases are intentionally left undefined — that is the variable we want to measure.
- [x] Drafted the **edited & combined prompt** (Variant 2): pinned `scan(List<String>, int) → Map<String, List<Integer>>` API, explicit `[A-Za-z]`-run tokenisation, 1-based line numbers with per-occurrence repetition, mandatory delegation to the three helpers, delimiter wrapping to kill the "thethem" false-positive class, `wordLength<=0` and null-line behaviour, Java 11 + no external deps, fenced-code-block-only output.
- [x] Both prompts and a per-edit rationale table are logged in `bookscan/logs/prompt_design_log.md`. The log also lists predicted weaknesses of Variant 1 (which Step 11 will check against actual model output) and operational notes for Step 3.

## Step 3 — Class generation with LLM A and LLM B
- [ ] Send the **unmodified-combined** prompt to both LLMs → save outputs to `bookscan/llm_a/unmodified/BookScan.java` and `bookscan/llm_b/unmodified/BookScan.java`.
- [ ] Send the **edited-combined** prompt to both LLMs → save outputs to `bookscan/llm_a/edited/BookScan.java` and `bookscan/llm_b/edited/BookScan.java`.
- [ ] Log every interaction (prompt + response + note on use) in `bookscan/logs/class_generation_log.md`.
- [ ] Add the `@Authors` header to every saved file.
- [ ] Commit each LLM × prompt-variant pair as its own step-tagged commit.

## Step 4 — Compile and smoke-run all four variants
- [ ] Compile each `BookScan.java` with `javac`; record compilation errors per variant.
- [ ] Write a tiny driver (`SmokeMain.java`) that exercises each method on a 5-line sample text; record runtime errors per variant.
- [ ] Save results into `bookscan/reports/smoke_results.json` (variant → compile/run status).

## Step 5 — Integration test generation with the agents
- [ ] For each of the four variants, ask the same agent to generate an `integration` JUnit-style test class (`BookScanIntegrationTest.java`) covering interactions between the three methods (e.g., `flipCase` → tokenise → `howManyTimes` per line; `strlen` filtering pipeline).
- [ ] Save under `bookscan/<llm>/<variant>/BookScanIntegrationTest.java`.
- [ ] Log all generation prompts/responses in `bookscan/logs/integration_test_generation_log.md`.

## Step 6 — Execute integration tests, collect raw metrics
- [ ] Run each `BookScanIntegrationTest.java` and record pass/fail counts per variant.
- [ ] Capture compilation errors, runtime exceptions, and stack traces in `bookscan/reports/integration_test_results.json`.
- [ ] Compute per-method pass rate (substring / strlen / flipCase) so the report can show which method tends to break under integration.

## Step 7 — Coverage analysis (re-use Phase-1 tooling)
- [ ] Re-use `tools/jacococli.jar` + `tools/jacocoagent.jar` to measure **branch and instruction coverage** of `BookScan` under each integration suite.
- [ ] Emit per-variant CSV into `bookscan/coverage_reports/<llm>_<variant>/` and a JSON summary into `bookscan/coverage_reports/summary.json`.
- [ ] **This was an outstanding gap from Phase 1's report request** — make sure both Phase-1 and Phase-2 coverage results land in the final report.

## Step 8 — JNose-guided test-smell inspection
- [ ] Apply the same JNose-guided checklist used in Phase 1 (Assertion Roulette, Eager Test, Duplicated Asserts, Magic Number Test, Useless Test) to each integration suite.
- [ ] Record findings in `bookscan/reports/test_smell_findings.md`, one section per variant.

## Step 9 — Black-box ECP / BVA assessment
- [ ] Build an ECP/BVA table for `BookScan`'s *integration* contract (not the individual methods): valid classes (empty text, single-line text, multi-line text, mixed case, target length 0, target length larger than every word, words with punctuation, repeated words across lines) + boundaries + invalid/out-of-contract.
- [ ] Score each variant's integration suite as **High / Medium / Low** using the same 80% / 50% rubric.
- [ ] Generate mutation-based additional tests (`BookScanBlackBoxTest.java`) where coverage is insufficient, using the mutation taxonomy from Phase 1 (empty/singleton, sign reversal, boundary substitution, etc.).
- [ ] Save to `bookscan/<llm>/<variant>/BookScanBlackBoxTest.java`; re-run and confirm they pass.

## Step 10 — Refactoring loop (only if test failures indicate a code bug)
- [ ] If any integration test reveals a real defect in `BookScan` (not a test gap), return to Step 3 with a corrective prompt that names the failing input and the expected output. Do **not** patch the generated code by hand.
- [ ] Re-record the corrective prompt in the logs and re-run Steps 4–9 for the regenerated variant.

## Step 11 — Statistical comparison of agent performance
- [ ] Build a comparison table: rows = {LLM A unmodified, LLM A edited, LLM B unmodified, LLM B edited}; columns = compile rate, integration test pass rate, branch coverage %, ECP effectiveness, # test smells.
- [ ] Plot at least one chart (bar or grouped bar) visualising pass rate and coverage per variant — saved as a PNG into `report/figures/` and `\includegraphics`-d in the report.
- [ ] Compute a simple statistic (mean ± stdev across variants, or paired comparison unmodified-vs-edited) so the report can answer "did prompt editing actually help?"

## Step 12 — Analyse complex-class integration failures
- [ ] For every failing integration test, write a one-paragraph root-cause analysis: was it a tokenisation disagreement, a case-normalisation order issue, an off-by-one in `howManyTimes`, etc.?
- [ ] Save as `bookscan/reports/failure_analysis.md` — the report's "problems in integration tests of complex classes" section pulls from this file.

## Step 13 — Extend the report to ≥ 8 pages (merge Phase 1 + Phase 2)
- [ ] Add a Phase-2 section to `report/report.tex` covering: prompt design (unmodified vs edited), `BookScan` generation, integration test results, coverage, smells, ECP/BVA, failure analysis, and statistical comparison.
- [ ] Insert the Phase-2 tables and the bar chart from Step 11.
- [ ] If Phase-1 coverage results are not already in the report's results section, fold them in here.
- [ ] Refresh the Acknowledgments with the **Phase-2 per-author duty split** (Phase-1 split is already there).
- [ ] Replace remaining placeholders that were noted in `report/README.md`:
  - real author names + student IDs
  - real LLM A name/version (no more "LLM A")
  - real GitHub repo URL in the conclusion
  - rewrite the literature-review section in the authors' own words (course rules forbid LLM-generated lit reviews)
- [ ] Verify page count ≥ 8 after `pdflatex → bibtex → pdflatex → pdflatex`.

## Step 14 — Reproducibility and submission hygiene
- [ ] Update `report/README.md` with Phase-2 compile/run instructions.
- [ ] Update root `README.md` (or create one if missing) describing how to run the full Phase-1 + Phase-2 pipeline end to end.
- [ ] Ensure every Phase-2 commit message follows the `"Phase 2 / Step N: <what & why>"` convention.
- [ ] Confirm `gemini_process/logs/`, `bookscan/logs/`, and any new prompt logs are tracked in git.
- [ ] Tag the final commit (e.g., `phase2-submit`) and confirm the Ninova upload contains the PDF report only — the code lives in the repo URL cited in the Acknowledgments.

---

## Out-of-scope reminders (do **not** do these)
- Do **not** modify the generated `Solution.java` files from Phase 1.
- Do **not** edit `BookScan.java` by hand after generation — go through a corrective prompt instead.
- Do **not** use any LLM tool to write the literature-review section.
- Do **not** bundle multiple steps into one commit or use vague commit messages.
