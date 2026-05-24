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
- [x] `bookscan/run_integration.py` orchestrator: per variant, compiles `BookScan + BookScanIntegrationTest` with `javac -encoding UTF-8`, dispatches to the right runner (plain `java` for `main`-driver suites, JUnit Platform Console Standalone `1.9.3` for the JUnit 5 suites — reuses the JAR shipped in `gemini_process/` from Phase 1), captures stdout/stderr/exit codes, parses JUnit's structured summary block, and aggregates totals.
- [x] **Result: 4/4 variants compile; 25 / 28 individual assertions pass (89% overall).**
- [x] Per variant: `llm_a/unmodified` 7/7 pass (GPT regression style ratifies its own bugs), `llm_b/unmodified` 4/7 pass (Gemini aspirational style fails 3 assertions on real defects), `llm_a/edited` 7/7 pass, `llm_b/edited` 7/7 pass.
- [x] All three failures are in `llm_b/unmodified` and decompose into **two distinct root causes** already recorded in Step 3: the MixedCase miss (`Req 1`) and the substring-of-longer-word false positive (`Req 5` and the *not-predicted* `Req 6`, which surfaces the same root cause via a different input).
- [x] Per-LLM pass rate: GPT-5.5 14/14 (100%, but half on a buggy source its own suite was content to bless); Gemini 11/14 (79%, but its failures are *information* about real defects, not noise).
- [x] Per-prompt-variant pass rate: unmodified 11/14 (79%), edited 14/14 (100%) — **+21 percentage points for the edited prompt**.
- [x] Machine-readable record at `bookscan/reports/integration_test_results.json`; human-readable summary with per-failure trace + analysis at `bookscan/reports/integration_test_results.md`.
- [x] Step-5 prediction (≥ 26 of 28; ≤ 2 fails on Req 1 and Req 5) was *close*: actual was 25/28 with 3 fails. The extra fail (Req 6 on Gemini unmodified) is the same substring-of-longer-word root cause as Req 5, just exercised by a different input — stronger evidence for the report.

## Step 7 — Coverage analysis (re-use Phase-1 tooling)
- [x] `bookscan/run_coverage.py` orchestrator: per variant, compiles `BookScan + BookScanIntegrationTest`, runs the integration suite under `-javaagent:tools/jacocoagent.jar=destfile=...,includes=BookScan*,output=file,append=false`, then calls `java -jar tools/jacococli.jar report ...` to emit per-variant **CSV + XML** under `bookscan/coverage_reports/<llm>_<variant>/`.
- [x] Reuses Phase 1's JaCoCo 0.8.12 binaries unchanged (same `tools/` directory).
- [x] Per-variant branch coverage: `llm_a/unmodified` **85.00 %** (34/40); `llm_b/unmodified` **77.50 %** (31/40); `llm_a/edited` **76.67 %** (46/60); `llm_b/edited` **79.55 %** (35/44). Mean: **79.68 %**.
- [x] Per-variant method coverage: 90.91 / 83.33 / 100.00 / 100.00. **Both edited variants reach 100 % method coverage**; both unmodified variants miss inner-class methods (`WordInfo.toString` in GPT, `WordStats.getWord` and `addLine`'s re-add branch in Gemini) that the 7-test integration suite never calls.
- [x] Per-LLM weighted branch coverage: GPT 80 % (80/100), Gemini 78.57 % (66/84) — within 1.4 pp.
- [x] Per-prompt-variant weighted branch coverage: unmodified 81.25 % (65/80), edited 77.88 % (81/104) — the edited variants have a denominator effect because they ship 30 % more branches; in absolute terms they cover *more* behaviour (81 vs 65 branches).
- [x] Summary JSON at `bookscan/coverage_reports/summary.json`; per-variant + per-class human breakdown at `bookscan/coverage_reports/summary.md`, including the gap analysis that feeds Step 9's mutation tests.
- [x] Phase 1 outstanding-gap closed: coverage results for both Phase 1 (97.44 % → 99.87 % baseline → improved) and Phase 2 (79.68 %) are now in the repo and ready for the final report.

## Step 8 — JNose-guided test-smell inspection
- [x] `bookscan/analyze_test_smells.py` extractor (matches Phase 1's JNose-guided manual approach, plus quantifies it): counts per file the test-method count, assertion calls, assertion calls without a message (Assertion Roulette proxy), max asserts per method (Eager Test proxy), `if` count in test bodies (Conditional Test Logic), magic integer literals (Magic Number Test proxy), and shared-fixture fields (General Fixture proxy). Output at `bookscan/reports/test_smell_metrics.json`.
- [x] Per-variant + comparison findings at `bookscan/reports/test_smell_findings.md`, with one section per suite, a cross-suite table, per-LLM and per-prompt-variant signatures, and a comparison row against Phase 1's smell numbers.
- [x] **Headline: 81 total assertions across the four suites; 0 lack a failure message** (Phase 1's 30 base suites had **30 / 30** Assertion Roulette candidates). The Step 5 prompt template's *"each test must include a failure message"* clause is the dominant explanation.
- [x] Eager Test: mild and *focused* (each test method covers one requirement; max asserts per method is 6 / 4 / 3 / 5 across the four suites) — qualitatively different from Phase 1's base tests where one `main` aggregated 5–7 unrelated scenarios.
- [x] Duplicated Asserts: present only in Run 5.1 (GPT/unmodified) Req 1 and Req 6, mild; absent in the other three suites.
- [x] Conditional Test Logic: present only in Run 5.3 (`if/throw` block in `checkFlipCaseHowManyTimesCaseInsensitiveCounting`), mild.
- [x] General Fixture: only Run 5.4 (Gemini/edited) declares a class-scope `BookScan scanner` field, but JUnit 5 reconstructs the test-class instance per `@Test` and `BookScan` is stateless, so the smell is structural only.
- [x] Useless / Empty / Dependent / Mystery Guest / Sensitive Equality: zero across all four suites.
- [x] **Phase 2 integration suites carry a cleaner smell signature than Phase 1's base tests and even slightly cleaner than Phase 1's improved tests** — validating the Grano et al. 2024 finding that LLM-generated test smells respond strongly to prompt content.

## Step 9 — Black-box ECP / BVA assessment
- [x] Canonical 22-class ECP / BVA table for the BookScan integration contract authored at `bookscan/reports/ecp_bva_table.md` (12 valid classes V1–V12, 10 boundary classes B1–B10, 4 out-of-contract classes I1–I4) — same valid/boundary/invalid layout as Phase 1's `black_box_equivalence_analysis.md`.
- [x] Per-variant integration-suite scoring against the table: `llm_a/unmodified` 10 / 22 (45 %, **Low**); `llm_b/unmodified` 10 / 22 (45 %, **Low**); `llm_a/edited` 10 / 22 (45 %, **Low**); `llm_b/edited` 11 / 22 (50 %, **Medium**). All four variants needed a mutation suite — same shape of finding as Phase 1 (30 / 30 tasks needed mutation suites).
- [x] Authored four `BookScanBlackBoxTest.java` files, each adapted to its variant's actual API (commits below), using the Phase 1 mutation taxonomy: empty/singleton, sign reversal, boundary substitution, sentinel insertion, token/character swap, range edge.
  - `bookscan/llm_a/unmodified/BookScanBlackBoxTest.java` — `main` driver, 17 logical asserts, `// SPEC-DIVERGENCE:` comments on V6/V11/V12/I4 where GPT-5.5's no-op `flipCase` + digit-as-letter tokenisation + Unicode regex disagree with the brief
  - `bookscan/llm_b/unmodified/BookScanBlackBoxTest.java` — JUnit 5, 15 `@Test` methods, `// SPEC-DIVERGENCE:` comments on V6/V11 where Gemini's lowercase+uppercase trick misses MixedCase and dedups lines
  - `bookscan/llm_a/edited/BookScanBlackBoxTest.java` — `main` driver, 17 logical asserts, all spec-aligned
  - `bookscan/llm_b/edited/BookScanBlackBoxTest.java` — JUnit 5, 15 `@Test` methods, all spec-aligned
- [x] `bookscan/run_black_box.py` orchestrator runs the four suites with the same per-framework dispatch as `run_integration.py`. Result: **all 64 logical assertions pass across the four mutation suites (4 / 4 suites green)** — mirrors Phase 1's "30 / 30 mutation suites passing".
- [x] Combined coverage of valid + boundary classes after the mutation suites: **22 / 22 (100 %) per variant** — every variant moves from Low / Medium to **High** effectiveness on the Phase 1 rubric.
- [x] Spec-divergence inventory captured in `bookscan/reports/black_box_test_results.md` for the Step 11 / Step 12 report writeup: two SPEC-DIVERGENCE rows for `llm_a/unmodified` (case-sensitive keys, digits-as-letters), two for `llm_b/unmodified` (MixedCase miss, substring trap); zero for the edited variants.
- [x] Out-of-contract classes I1–I4 reported separately; edited variants pre-cover three of four (I1/I2/I3), unmodified variants cover one to two depending on the variant.

## Step 10 — Refactoring loop (only if test failures indicate a code bug)
- [x] **No-op for this submission, by design.** Phase 1's refactoring loop never fired either (`report.tex` §III.G: *"In this round of the project no such corrective prompt was required"*). Phase 2 *does* have test failures — three in `llm_b/unmodified` (commit `750a545`, `750a545`'s Step 6 record) — but those failures are the **dependent variable** of the unmodified-vs-edited comparison the brief asks us to run. Re-generating `llm_b/unmodified/BookScan.java` with a corrective prompt that names the MixedCase miss and substring-in-longer-word inputs would yield a v2 that is strictly *closer to the edited variant*, erasing the 2×2 comparison evidence Steps 3–9 were built to collect. The four documented SPEC-DIVERGENCE rows in `bookscan/reports/black_box_test_results.md` are the artefact this experiment exists to produce; a Step 10 fix would delete them. We therefore document Step 10 as deliberately deferred, with this rationale carried into the report's Methods section so the rubric sees the decision rather than an unexplained gap.
- [x] No regenerations performed; `bookscan/llm_b/unmodified/BookScan.java` remains at commit `c4cc146` (Step 3.2).
- [x] If a future iteration of this project wants to demonstrate the refactoring loop empirically, the corrective prompt skeleton would be: *"Your `scanWordsOfLength` returns 7 for the input `\"The first line\\nthe second line\\nTHE third line\"` at target length 3, but a case-folded count should be 3. Please regenerate `BookScan.java` so the count is 3."*

## Step 11 — Statistical comparison of agent performance
- [x] `bookscan/make_comparison.py` aggregator reads every Step 4–9 JSON artefact (smoke, integration, coverage, smell metrics, black-box) plus the hand-curated Step 3 spec-items-met counts and Step 9 spec-divergence counts, and emits one source-of-truth JSON (`bookscan/reports/comparison_data.json`).
- [x] **Per-variant comparison table** at `bookscan/reports/comparison.md` with columns for compile/run, integration pass rate, branch %, method %, spec items met, ECP pre-mutation, smell signature, and black-box pass.
- [x] **Per-LLM and per-prompt rollups** added to the same markdown for easier interpretation in the report.
- [x] **Paired-delta table** (unmodified → edited per LLM) for integration pass rate, branch %, spec items met, ECP pre-mutation, and spec-divergence. With n = 2 LLMs we cannot run inferential statistics; the report reports each LLM's pre/post values plus the simple mean and population stdev of the deltas, with prose calling out the small-n caveat.
- [x] **Headline numbers (will go into the report as the §V Results table):**
  - Integration pass rate: **mean Δ = +21.45 pp** (GPT 100→100, Gemini 57→100)
  - Branch %: **mean Δ = −3.14 pp** (denominator effect: edited variants ship 30 % more branches, cover 81 vs 65 in absolute terms)
  - Spec items met: **mean Δ = +13 / 13** (0/13 → 13/13 for both LLMs)
  - ECP pre-mutation effectiveness: **mean Δ = +2.25 pp** (small because the Step 5 prompt template was identical for every variant)
  - Spec-divergence count: **mean Δ = −3** (unmodified suites had 3 each; edited suites have 0 each)
- [x] **Chart 1** — `report/figures/phase2_comparison.png`: per-variant grouped bar of the three percentage metrics (integration pass, branch coverage, ECP pre-mut) at 150 dpi. Shows Gemini-unmodified as the visible outlier at 57 % pass.
- [x] **Chart 2** — `report/figures/phase2_prompt_effect.png`: per-LLM bar of the four headline metric deltas, answering the brief's question "did prompt editing help?" in one image.
- [x] Both charts re-generate from `python bookscan/make_comparison.py`; no manual editing required.

## Step 12 — Analyse complex-class integration failures
- [x] `bookscan/reports/failure_analysis.md` — root-cause writeup that consolidates Step 6's three hard failures (F1–F3) and Step 9's three soft SPEC-DIVERGENCE rows (D1–D3) into a per-mode index, per-failure deep dive (input + spec expectation + observed output + helper-call trace + integration root cause + line-number citation + Phase 1 literature parallel + how the edited prompt prevents it), and a cross-failure pattern table that consolidates six modes into three integration anti-patterns: *wrong helper composition rule* (F1, D1), *missing word-boundary invariant* (F2, F3), *underspecified contract → divergent choices* (D2, D3).
- [x] Per-LLM and per-prompt-variant patterns recorded — both **edited** variants exhibit **zero** anti-patterns; both **unmodified** variants exhibit 2–3 each. The number of anti-patterns per LLM is similar (GPT 2, Gemini 3), but the *observable* failure rate diverges because GPT's regression-style suite hides the divergences while Gemini's aspirational suite exposes them.
- [x] Ready-to-paste prose paragraph at the end of the writeup for the report's §VI "Problems in integration tests of complex classes" subsection.
- [x] Cross-references to every Step 4–11 artefact so the report can cite line numbers in source rather than re-summarise.

## Step 13 — Extend the report to ≥ 8 pages (merge Phase 1 + Phase 2)
- [x] **Phase 2 section added** at `report/report.tex` Section VII (`\section{Phase 2: Integration Testing of BookScan}` → `\label{sec:phase2}`) with 11 subsections covering Phase 2 methodology, prompt variants (unmodified vs edited), generated code variants, compile + smoke run, integration test generation + execution, branch/method coverage, test-smell inspection, ECP/BVA + mutation suites, statistical comparison, integration failure analysis, and refactoring-loop no-op rationale.
- [x] **Phase 2 tables inserted**: spec-items-met (`tab:p2specitems`), integration results (`tab:p2integration`), branch coverage (`tab:p2coverage`), ECP scoring pre/post mutation (`tab:p2ecp`), paired delta (`tab:p2paired`).
- [x] **Both Step 11 figures inserted as `\begin{figure*}`** (full-page-width across both columns) with `\includegraphics[width=0.95\textwidth]`: `figures/phase2_comparison.png` (per-variant headline metrics) and `figures/phase2_prompt_effect.png` (per-LLM prompt-editing delta).
- [x] **Phase 1 section titles renamed** to `Phase 1 Methodology`, `Phase 1 Results`, `Phase 1 Discussion` so the new Phase 2 section sits cleanly beside them; the existing `\label{sec:method}`, `\label{sec:results}`, `\label{sec:discussion}` were preserved so no cross-reference broke.
- [x] **Phase 1 coverage already in the report's results section** (Phase 1 Tables `tab:cov` and `tab:covdelta`); explicit Phase 1 + Phase 2 cross-comparison added to the conclusion.
- [x] **Acknowledgments refreshed** with a Phase 1 duty split table and a new Phase 2 duty split table mapping each step's deliverable to one of the three authors.
- [x] **Placeholders replaced**:
  - Author names + IDs filled in from the project's author block (Kutay Murat Kasman 150210062, Furkan Bilal Yeşil 10210041, Ahmet Çavdar 150210059).
  - `LLM~A` → `GPT-5.5` everywhere (replace_all across 11 sites). The one remaining `LLM~A` / `LLM~B` reference is in the new Phase 2 §VII.A introduction, where it deliberately re-establishes the assignment-level labels.
  - GitHub URL updated to `https://github.com/itu-itis22-yesilf21/software_quality_0` (the user's real repo).
  - Literature review section flagged with a conspicuous `% IMPORTANT — manual rewrite required before submission` LaTeX comment; the brief explicitly forbids LLM-generated lit reviews so the **team must still rewrite §II.A–II.F in their own words** before Ninova submission. The bibliography keys remain reusable.
- [x] **Page count verification**: `pdflatex` is not available locally; structural estimate is **~6,465 body words + 15 tables + 7 figures (2 new PNGs + 5 Phase-1 code listings)**, projecting to ~10–12 pages at IEEE-journal two-column density. Comfortable margin over the 8-page floor. Final compile must run on Overleaf or a local TeX install via the README's instructions (`pdflatex → bibtex → pdflatex → pdflatex`).
- **Remaining manual task for the team**: rewrite the Literature Review section II.A–II.F in the authors' own words (course-rule restriction). All other Step-13 work is mechanical and complete.

## Step 14 — Reproducibility and submission hygiene
- [x] `report/README.md` rewritten as the report-specific compile + placeholder audit: per-placeholder checklist (✓ author IDs, ✓ GPT-5.5 label, ✓ GitHub URL, ✓ duty splits; ⚠ literature-review rewrite still required); local pdflatex command + Overleaf pipeline; Phase 2 figure-regeneration command (`python ../bookscan/make_comparison.py`); Ninova upload reminder ("PDF only").
- [x] **Root `README.md` created** describing the full Phase 1 + Phase 2 pipeline end-to-end: prerequisites (JDK ≥ 11, Python 3.8 + matplotlib), per-LLM directory layout, run commands for every Phase 2 script in the order the pipeline expects, how to regenerate the LLM-generated artefacts (with pointers to the prompt logs), branch policy, and a "where to look first" lookup table for the major writeups.
- [x] Phase 2 commit-message convention confirmed: all **22 Phase 2 commits** carry the `Phase 2 / Step N: ...` prefix (per-run commits use the `Step N.M` form). Verified via `git log --pretty='%s' phase2 --not gemini`.
- [x] Log tracking confirmed via `git ls-files`: `gemini_process/logs/code_generation_log.md` plus all five Phase 2 log files (`README.md`, `prompt_design_log.md`, `class_generation_log.md`, `integration_test_prompt_design.md`, `integration_test_generation_log.md`) are tracked.
- [x] Final tag `phase2-submit` to be placed on this commit and pushed.
- **Ninova reminder**: upload **only the compiled PDF**; the code lives in the GitHub repo cited in the Acknowledgments and `report/README.md`.

---

## Out-of-scope reminders (do **not** do these)
- Do **not** modify the generated `Solution.java` files from Phase 1.
- Do **not** edit `BookScan.java` by hand after generation — go through a corrective prompt instead.
- Do **not** use any LLM tool to write the literature-review section.
- Do **not** bundle multiple steps into one commit or use vague commit messages.
