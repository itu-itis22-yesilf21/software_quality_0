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
- [x] Variant 1 (unmodified+combined) → GPT-5.5 → `bookscan/llm_a/unmodified/BookScan.java` (Run 1, commit `2a71037`). Model invented `scanWords` returning `Map<String, WordInfo>`, `[^\p{L}\p{N}]+` tokenisation, no-op `flipCase(flipCase(x))` normalisation, synthesised-stream `howManyTimes`. 0/13 Variant 2 spec items.
- [x] Variant 1 (unmodified+combined) → Gemini 3.1 Pro → `bookscan/llm_b/unmodified/BookScan.java` (Run 2, commit `c4cc146`). Model invented `scanWordsOfLength` returning `Map<String, WordStats>`, `[^a-zA-Z]+` tokenisation, lowercase + flipCase variant counting that misses MixedCase words, substring-of-longer-word false positives. 0/13 Variant 2 spec items.
- [x] Variant 2 (edited+combined) → GPT-5.5 → `bookscan/llm_a/edited/BookScan.java` (Run 3, commit `fadb4a8`). Exact `scan(List<String>,int)→Map<String,List<Integer>>` signature, `[A-Za-z]` runs via `isAsciiLetter`, real case folding via `normalise()→flipCase`, ``-wrapped per-line `howManyTimes`, per-occurrence line numbers. **13/13** spec items.
- [x] Variant 2 (edited+combined) → Gemini 3.1 Pro → `bookscan/llm_b/edited/BookScan.java` (Run 4, commit `3ac4ca1`). Same exact API; `split("[^A-Za-z]+")` tokenisation, `normalizeToLowercase→flipCase`, ``-wrapped counting, per-occurrence line numbers. **13/13** spec items.
- [x] Every prompt + response + use-note logged in `bookscan/logs/class_generation_log.md`, with per-run spec-compliance tables and Run-1-vs-3, Run-2-vs-4, Run-3-vs-4 comparison tables for the Step 11 analysis.
- [x] `@Authors` header (Kutay Murat Kasman 150210062, Furkan Bilal Yeşil 10210041, Ahmet Çavdar 150210059) present in all four files.
- [x] One commit per LLM × variant (`Phase 2 / Step 3.1`–`3.4`).
- **Headline result:** both LLMs score 0/13 under the unmodified prompt and 13/13 under the edited prompt, with edited outputs differing only in idiom (LinkedHashMap vs HashMap, regex vs char walker, `Character.isUpperCase` vs range checks). This is the central piece of evidence for the prompt-engineering story Step 11 will present.

## Step 4 — Compile and smoke-run all four variants
- [x] Per-variant `SmokeMain.java` saved alongside each `BookScan.java`. The two unmodified drivers call the model-invented APIs (`scanWords` / `scanWordsOfLength`); the two edited drivers also assert the spec-pinned `scan(...)` output for a shared 5-line text.
- [x] Orchestrator at `bookscan/run_smoke.py` cleans stale `.class` files, runs `javac -encoding UTF-8 BookScan.java SmokeMain.java`, runs `java SmokeMain`, captures stdout/stderr/return-code per variant.
- [x] Java toolchain confirmed: `javac 21.0.10` (Java 11 source target — same as Phase 1).
- [x] **All four variants compile and run** (`4/4 compile_ok`, `4/4 run_ok`).
- [x] **All six HumanEval helper assertions pass on all four variants** (`howManyTimes("", "a") == 0`, `howManyTimes("aaa", "a") == 3`, `howManyTimes("aaaa", "aa") == 3`, `strlen("") == 0`, `strlen("abc") == 3`, `flipCase("Hello") == "hELLO"`).
- [x] **Both edited variants additionally pass 9 spec-pinned integration assertions**, including per-occurrence line-number lists (`the=[1,1,2,3,4,5,5,5]`), case-insensitive collapse (`mat=[1,2]`), and null/zero/negative edge cases.
- [x] Aggregated results saved to `bookscan/reports/smoke_results.json` (machine) and `bookscan/reports/smoke_results.md` (human, with per-variant integration output + interpretation).
- [x] Build artefacts ignored via root `.gitignore` (`*.class`, `*.exec`).
- **Key observations carried into Step 5:** GPT-5.5 unmodified shows case-sensitive keys (`The` vs `the` vs `MAT` vs `mat` all separate) because its `flipCase(flipCase(x))` is a no-op; Gemini unmodified collapses keys to lowercase but misses MixedCase words (`Count: 7` for `the` on the sample, where 8 is expected because `"The"` on line 1 contributes 0). Both edited variants produce the same integration output, differing only in `HashMap` vs `LinkedHashMap` iteration order.

## Step 5 — Integration test generation with the agents
- [x] Shared template authored in `bookscan/logs/integration_test_prompt_design.md` with seven concrete interaction requirements; instantiated once per variant by inlining that variant's actual `BookScan.java` source (constant prompt, varying source — keeps Step 11 attribution clean).
- [x] Run 5.1: GPT-5.5 on `llm_a/unmodified` source → `bookscan/llm_a/unmodified/BookScanIntegrationTest.java` (commit `7ebaa7d`). `main`-driver style, seven `requirementN_*` methods, **regression-style** (ratifies the bugs as expected behaviour: `The/the/THE` and `Cat/cat/CAT` asserted as separate keys).
- [x] Run 5.2: Gemini 3.1 Pro on `llm_b/unmodified` source → `bookscan/llm_b/unmodified/BookScanIntegrationTest.java` (commit `9b46958`). JUnit 5 (`@Test`) style, **aspirational** (asserts the spec even where the source contradicts it; the model even hangs a comment "*Note: This will correctly expose the bug where flipCase and howManyTimes fail on 'The'*"). Req 1 and Req 5 expected to FAIL in Step 6 against this BookScan.
- [x] Run 5.3: GPT-5.5 on `llm_a/edited` source → `bookscan/llm_a/edited/BookScanIntegrationTest.java` (commit `6d124b9`). Same `main`-driver style; spec-aligned because the source is spec-compliant.
- [x] Run 5.4: Gemini 3.1 Pro on `llm_b/edited` source → `bookscan/llm_b/edited/BookScanIntegrationTest.java` (commit `b7b7888`). JUnit 5 style; spec-aligned and **slightly more adversarial** than Run 5.3 (adds `tomcat`/`catatonic` trap words for the substring case, plus an explicit `singletonList(null)` null-line case).
- [x] Per-run logs at `bookscan/logs/integration_test_generation_log.md` include the full prompt+source reference, model details, full response, and per-requirement expectations.
- **Headline finding for Step 11.** *Test framework is a model-stable choice* — GPT picked the `main` driver both times, Gemini picked JUnit 5 both times. *Test correctness is a source-shape effect* — under flawed source, GPT regresses while Gemini asserts the spec; under clean source, both converge. *Step 6 prediction*: ≥ 26 of 28 assertions pass; the ≤ 2 expected fails are Run 5.2 Req 1 and Req 5.

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
