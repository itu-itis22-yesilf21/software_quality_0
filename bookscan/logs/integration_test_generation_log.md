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

## Run 5.2 — `llm_b/unmodified/BookScan.java` → Gemini 3.1 Pro  *(complete)*

**Prompt sent.** The shared template from
`bookscan/logs/integration_test_prompt_design.md` at commit `1d3b3e1`,
with `bookscan/llm_b/unmodified/BookScan.java` from commit `c4cc146`
(157 lines, including the `@Authors` header) inlined under the source
marker. Sent as a single turn to Gemini 3.1 Pro.

**Model + access details.** Gemini 3.1 Pro via its hosted chat UI,
default sampling, run on 2026-05-24. Single turn, no follow-ups.

**Response received.** Verbatim Java source saved to `response5_2.txt`
and copied byte-for-byte. The paste preserved all asterisks. Full
source committed at
[`bookscan/llm_b/unmodified/BookScanIntegrationTest.java`](../llm_b/unmodified/BookScanIntegrationTest.java).

**How the output was used.** Saved to
`bookscan/llm_b/unmodified/BookScanIntegrationTest.java`, no edits.
`response5_2.txt` deleted in the same commit.

**First observations (Step 11 data).** Gemini chose **JUnit 5
(`@Test` + Jupiter assertions)** instead of the `main` driver style.
The most important behavioural signal is the opposite of Run 5.1:
**Gemini wrote tests that assert the brief's intended semantics, even
though the BookScan.java under test does not satisfy them**. The
suite is therefore aspirational, and several tests are expected to
fail when run against this BookScan in Step 6:

| Requirement | What Gemini asserts | Expected outcome against this BookScan |
|---|---|---|
| 1 (case-fold) | `"the"` key with `getTotalOccurrences()==3` and `getLines()==[1,2,3]` for `"The/the/THE"` mix | **FAIL** — model's `howManyTimes(line, word) + howManyTimes(line, flipCase(word))` never matches MixedCase `"The"`, so totalOccurrences will be 2 and lines will be [2, 3] |
| 4 (flipCase × howManyTimes) | `"java"` key with `getTotalOccurrences()==2` for `"JAVA java"`, plus `howManyTimes(text, "java") == 1` to prove the single-case path is wrong | **PASS** — both checks happen to hold because Gemini's add-lowercase-plus-add-uppercase trick gets `1+1=2` here |
| 5 (substring-in-longer-word) | `"cat"` key with `getTotalOccurrences()==1` for `"cat concatenate"` | **FAIL** — Gemini's raw `howManyTimes("cat concatenate", "cat")` matches the substring inside `"concatenate"` too, so the count will be 2 |

The model even hangs a comment off Requirement 1: *"Note: This will
correctly expose the bug where flipCase and howManyTimes fail on
'The'."* Gemini knowingly authored a failing test as a bug demonstration.

This is the headline behavioural contrast for Step 11:

- GPT-5.5 (Run 5.1): **regression** style — tests ratify whatever the
  source does
- Gemini 3.1 Pro (Run 5.2): **aspirational** style — tests assert the
  spec even when the source contradicts it

Both LLMs were handed the exact same prompt and source; only the model
identity differs.

---

## Run 5.3 — `llm_a/edited/BookScan.java` → GPT-5.5  *(complete)*

**Prompt sent.** Shared template from
`bookscan/logs/integration_test_prompt_design.md` at commit `1d3b3e1`,
with `bookscan/llm_a/edited/BookScan.java` from commit `fadb4a8`
(155 lines, including `@Authors`) inlined under the source marker.
Sent as a single turn to GPT-5.5.

**Model + access details.** GPT-5.5 via its hosted chat UI, default
sampling, run on 2026-05-24. Single turn, no follow-ups.

**Response received.** Verbatim Java source, saved to `response5_3.txt`
and copied byte-for-byte. Asterisks preserved. Full source committed at
[`bookscan/llm_a/edited/BookScanIntegrationTest.java`](../llm_a/edited/BookScanIntegrationTest.java).

**How the output was used.** Saved to
`bookscan/llm_a/edited/BookScanIntegrationTest.java`, no edits.
`response5_3.txt` deleted in the same commit.

**First observations (Step 11 data).** GPT-5.5 again chose the
`public static void main` driver style (same as Run 5.1), with seven
labelled `check*` methods and `assertListEquals` / `assertMapEquals`
helpers. With the spec-compliant Variant 2 source under test, all
assertions are spec-aligned and should pass:

| Requirement | Assertion | Notes |
|---|---|---|
| 1 (case-fold) | `actual.get("the") == [1, 2, 3]` for `"The/the/THE"` across three lines | One occurrence per line, lowercase key, per-occurrence ordering — exactly the Variant 2 contract |
| 2 (length filter) | `expected.put("cat", [1])`, `("dog", [1])`, `("cow", [2])` with no other keys | Lowercase keys; words of other lengths absent |
| 3 (repeated word) | `actual.get("red") == [1, 1, 1]` for `"red blue red red green"` | Per-occurrence repetition, ascending |
| 4 (case-insensitive) | `actual.get("dog") == [1, 1, 1]` for `"DOG dog DoG"`; explicit `assertFalse(containsKey("DOG"))` and a single-case sanity check (`scan(["dog"], 3).get("dog") == [1]`) | Pins both the positive case (collapse) and the negative case (no leftover variants) |
| 5 (substring-in-longer-word) | `actual.get("cat") == [1]` for `"cat concatenate"` | Validates the ``-wrapped `howManyTimes` |
| 6 (one-char words) | lowercase keys `i`, `a`, `b`, `c` with per-line lists | Spec-aligned |
| 7 (edges) | empty list, `wordLength=0`, `wordLength=10` all return empty map | Spec-aligned |

Compared to Run 5.1, GPT-5.5 produces materially **stronger** tests
here even though we used the same model and the same prompt template,
because the source it is testing is itself spec-compliant. The model
mirrors the source's contract instead of mirroring the source's bugs.
This reinforces the Step 3 finding: when the source is well-specified,
the downstream tests inherit that quality.

---

## Run 5.4 — `llm_b/edited/BookScan.java` → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/edited/BookScanIntegrationTest.java`._
