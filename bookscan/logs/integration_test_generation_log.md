# Phase 2 / Step 5 — Integration Test Generation Log

This log captures every LLM interaction that produced a
`BookScanIntegrationTest.java`. Format mirrors Phase 1's
`gemini_process/logs/code_generation_log.md` and Phase 2 / Step 3's
`class_generation_log.md`.

Each section records:

1. **Prompt sent** — the shared template from
   `bookscan/logs/integration_test_prompt_design.md` with this
   variant's `BookScan.java` source inlined under the
   `<INLINE THE FULL BookScan.java SOURCE HERE>` marker. We record the
   source byte length so the inlining is verifiable later.
2. **Model + access details** — model label, UI used, date, sampling.
3. **Response received** — verbatim, including any prose the model
   added outside the requested fenced code block.
4. **How the output was used** — saved path, any post-processing
   (e.g., paste-artefact restoration).

The four runs in this step:

| # | Source variant | LLM | Save path |
|---|---|---|---|
| 5.1 | `llm_a/unmodified/BookScan.java` | GPT-5.5         | `bookscan/llm_a/unmodified/BookScanIntegrationTest.java`  |
| 5.2 | `llm_b/unmodified/BookScan.java` | Gemini 3.1 Pro  | `bookscan/llm_b/unmodified/BookScanIntegrationTest.java`  |
| 5.3 | `llm_a/edited/BookScan.java`     | GPT-5.5         | `bookscan/llm_a/edited/BookScanIntegrationTest.java`      |
| 5.4 | `llm_b/edited/BookScan.java`     | Gemini 3.1 Pro  | `bookscan/llm_b/edited/BookScanIntegrationTest.java`      |

Each run gets its own step-tagged commit (`Phase 2 / Step 5.<run#>: ...`).

---

## Run 5.1 — `llm_a/unmodified/BookScan.java` → GPT-5.5  *(complete)*

**Prompt sent.** The "Shared template" block from
`bookscan/logs/integration_test_prompt_design.md` at commit `1d3b3e1`,
with the full `bookscan/llm_a/unmodified/BookScan.java` source from
commit `2a71037` (161 lines, including the `@Authors` header) pasted
into the `<INLINE THE FULL BookScan.java SOURCE HERE>` marker. Sent as
a single turn to GPT-5.5.

**Model + access details.** GPT-5.5 via its hosted chat UI, default
sampling, run on 2026-05-24. Single turn, no follow-ups.

**Response received.** Verbatim Java source saved to `response5_1.txt`,
copied byte-for-byte into the integration test file. The paste
preserved all asterisks; no post-processing was needed. Full source is
the file under
[`bookscan/llm_a/unmodified/BookScanIntegrationTest.java`](../llm_a/unmodified/BookScanIntegrationTest.java)
committed alongside this log entry.

**How the output was used.** Saved to
`bookscan/llm_a/unmodified/BookScanIntegrationTest.java`, no edits.
Transient `response5_1.txt` deleted in the same commit.

**First observations (Step 11 data).** GPT-5.5 chose a
`public static void main` driver with seven labelled `requirementN_*`
methods and a private `check(condition, message)` helper. The most
important behavioural signal is that **GPT-5.5 read the BookScan.java
source carefully and wrote tests that bake in the model's bugs as
expected behaviour**, rather than asserting the integration brief's
intended semantics. Examples:

| Requirement | Expected by brief | What GPT-5.5 asserts |
|---|---|---|
| 1 (case-fold) | one key `"the"`, value covering all three lines | three separate keys `"The"`, `"the"`, `"THE"`, each with `lines=[1]/[2]/[3]` |
| 4 (flipCase × howManyTimes) | one key `"cat"` with count 3 | three separate keys `"Cat"`, `"cat"`, `"CAT"`, each with count 1 |
| 6 (one-char words) | lowercase keys `"i"`, `"a"`, `"b"`, `"c"` | case-preserved keys `"I"`, `"a"`, `"b"`, `"C"` |

This is **regression-style** testing, not spec-conforming testing.
Requirement 4 even includes two extra `howManyTimes` calls that
demonstrate the case-variant difference (`exactCaseOnlyCount=1` vs
`caseFoldedCount=3`), explicitly framing the case-sensitive behaviour
as the desired one rather than a bug. Step 6 will run this suite; we
expect it to pass against this specific BookScan because both the
suite and the source share the same (flawed) view of correctness.

Tests 5 (substring inside longer word), 6 (one-character words), and 7
(empty/zero/too-long edge cases) are spec-aligned because the
unmodified BookScan happens to handle those correctly even without
case folding.

---

## Run 5.2 — `llm_b/unmodified/BookScan.java` → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/unmodified/BookScanIntegrationTest.java`._

---

## Run 5.3 — `llm_a/edited/BookScan.java` → GPT-5.5  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_a/edited/BookScanIntegrationTest.java`._

---

## Run 5.4 — `llm_b/edited/BookScan.java` → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/edited/BookScanIntegrationTest.java`._
