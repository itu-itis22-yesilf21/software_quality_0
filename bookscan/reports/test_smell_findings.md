# Phase 2 / Step 8 — JNose-Guided Test-Smell Inspection

## Methodology

We apply the **same JNose-guided manual checklist used in Phase 1**
(see `improved_test_results.md` and Phase 1 report Section IV.E).
JNose itself is not run for the same reason as Phase 1:

> No local `jnose` command or Docker runtime was available, and JNose
> targets annotated JUnit projects while two of our integration suites
> (`llm_a/unmodified` and `llm_a/edited`) use plain `public static
> void main` drivers rather than `@Test` methods.

To make the per-variant claims **quantitative and reproducible**, we
added `bookscan/analyze_test_smells.py`, a regex-based metric
extractor that counts the structural indicators of each smell. Its
output lives at `bookscan/reports/test_smell_metrics.json` and is the
ground truth for every number in this document. The qualitative
"why does this matter" judgements are added on top of those numbers.

### JNose smell catalogue applied here

| Smell | Definition | How we detect it |
|---|---|---|
| **Assertion Roulette**      | Test class has multiple assertions without explanatory messages, so the failing assertion is hard to identify. | Count of assertion calls whose argument list does not end in a string literal. Zero across all four suites. |
| **Eager Test**              | One test method exercises multiple production methods or scenarios. | Maximum number of assertion calls inside any single test method. Phase 1 base tests had 5–7 unrelated checks per `main`; we use "max asserts per method" as the proxy. |
| **Duplicated Asserts**      | The same assertion shape is repeated across tests. | Manual review for near-identical assertion blocks. |
| **Magic Number Test**       | Integer literals are used as expected values without naming. | Count of integer literals ≥ 2 inside test bodies (we exclude 0 and 1 as too common to flag). |
| **Conditional Test Logic**  | `if/else` or loops inside a test body. | Count of `if (` occurrences inside test bodies. |
| **General Fixture**         | A shared fixture (class field) is bigger than any single test needs. | Detect any `private (final)? BookScan <name> = new BookScan();` field at class scope. |
| **Useless Test**            | A test that does nothing or makes only trivially-true assertions. | Manual review: any test with zero assertions or assertions that cannot fail. |
| **Mystery Guest**           | The test depends on external resources (files, DB, network). | Manual review: zero across the suites — all inputs are inline literals. |
| **Sensitive Equality**      | Equality check based on `toString()`. | Manual review: zero. |
| **Empty Test**              | A test method with an empty body. | Manual review: zero. |
| **Dependent Test**          | A test depends on another test's effects. | Manual review: zero — each test instantiates its own `BookScan`. |

## Per-variant structural metrics (from `analyze_test_smells.py`)

| Variant | Lines | Test methods | Total asserts | No-message asserts | Max asserts / test | `if` in tests | Magic ints |
|---|---:|---:|---:|---:|---:|---:|---:|
| `llm_a/unmodified` (GPT-5.5)        | 188 | 7 | 28 | 0 | 6 | 0 | 17 |
| `llm_b/unmodified` (Gemini 3.1 Pro) | 146 | 7 | 22 | 0 | 4 | 0 | 15 |
| `llm_a/edited`     (GPT-5.5)        | 192 | 7 | 10 | 0 | 3 | 1 | 13 |
| `llm_b/edited`     (Gemini 3.1 Pro) | 129 | 7 | 21 | 0 | 5 | 0 | 13 |
| **Total**                           | 655 | 28 | **81** | **0** | – | 1 | 58 |

## Per-suite qualitative findings

### `llm_a/unmodified/BookScanIntegrationTest.java` (GPT-5.5, `main` driver, 188 lines)

- **Assertion Roulette: NO.** All 28 `check(...)` calls take a
  `"Requirement N failed: ..."` message and the custom helpers
  `assertWordInfo` / `listOf` propagate that message.
- **Eager Test: MILD.** The longest method is
  `requirement1_tokenisationCaseFoldingMatching` with 6 assertions, all
  testing different facets of the same case-fold scenario. None of the
  seven requirement methods mix unrelated production responsibilities,
  so this is *focused multi-assert* rather than the Phase-1 base-test
  pattern where one `main` aggregated five totally unrelated scenarios.
- **Duplicated Asserts: PRESENT.**
  - In Req 1, three near-identical lines (`logs/integration_test_generation_log.md` Run 5.1):
    ```java
    assertWordInfo(result, "The", 1, listOf(1), ...);
    assertWordInfo(result, "the", 1, listOf(2), ...);
    assertWordInfo(result, "THE", 1, listOf(3), ...);
    ```
  - In Req 6, four near-identical lines for `"I" / "a" / "b" / "C"`.
  - The duplication is structural (different inputs, same expected
    shape) and could be flattened to a parameterised assertion, but
    the helper `assertWordInfo` already abstracts the body, so the
    smell is mild.
- **Magic Number Test: PRESENT (17 literals).** Each
  `assertWordInfo(result, X, 1, listOf(Y))` hard-codes line and count
  numbers. A spec-correct refactor would name them (`EXPECTED_LINE_1`),
  but the brief asks for one labelled assertion per requirement and
  the numbers are tightly bound to the small inline test text, so the
  cost of removing the smell is higher than the benefit.
- **Conditional Test Logic: NO.**
- **General Fixture: NO** (each method calls `new BookScan()` locally).
- **Useless / Empty / Dependent / Mystery Guest / Sensitive Equality: NO.**
- **Notable signature:** GPT-5.5 ratifies the source's case-sensitive
  bug as expected behaviour (Step 5 analysis). From a smell standpoint
  this is **not** a smell on its own — the tests are honest about
  what they assert — but it is an *anti-pattern* worth recording: a
  spec-conforming reviewer would flag the entire Req 1 / Req 4 / Req
  6 block as a Magic Number Test where the *meaning* of the magic is
  the bug.

### `llm_b/unmodified/BookScanIntegrationTest.java` (Gemini 3.1 Pro, JUnit 5, 146 lines)

- **Assertion Roulette: NO.** All 22 Jupiter assertions
  (`assertEquals/assertTrue/assertFalse/assertNotNull/assertNotEquals`)
  take a message string.
- **Eager Test: MILD.** Longest method is
  `testFlipCaseHowManyTimesIntegration` (4 asserts); the median test
  has 3. Smaller than Run 5.1 in this proxy because Gemini wrote
  shorter tests.
- **Duplicated Asserts: NO** (each test has distinct assertion targets).
- **Magic Number Test: PRESENT (15 literals).** Counts of `2`, `3`,
  `5`, `50`, `1` appear directly. Same trade-off as Run 5.1.
- **Conditional Test Logic: NO.**
- **General Fixture: NO** (each `@Test` creates `new BookScan()`).
- **Useless / Empty / Dependent / Mystery Guest / Sensitive Equality: NO.**
- **Notable signature:** Gemini explicitly comments its Req 1 as
  *"This will correctly expose the bug where flipCase and howManyTimes
  fail on 'The'."* That is not a smell — it is a deliberate
  documentation comment — and it is *the opposite* of GPT-5.5's
  bug-ratifying stance. JNose would not flag it.

### `llm_a/edited/BookScanIntegrationTest.java` (GPT-5.5, `main` driver, 192 lines)

- **Assertion Roulette: NO.** All 10 assertion calls have messages.
  The lower assertion count (10 vs 28 in Run 5.1) is explained by the
  use of `assertListEquals` / `assertMapEquals` helpers that perform a
  single `equals` check on the whole expected/actual map instead of
  asserting one cell at a time.
- **Eager Test: MINIMAL.** Longest test has 3 assertions; the median
  is 1.
- **Duplicated Asserts: NO.**
- **Magic Number Test: PRESENT (13 literals).** Same trade-off as
  Run 5.1.
- **Conditional Test Logic: PRESENT — 1 occurrence.**
  `checkFlipCaseHowManyTimesCaseInsensitiveCounting` contains:
  ```java
  if (actual.containsKey("DOG") || actual.containsKey("DoG")) {
      throw new AssertionError(...);
  }
  ```
  This is semantically equivalent to a single negative `assertFalse`
  with an OR'ed condition, but the `if`/`throw` form is what JNose
  flags. **Severity: low** — the failure message and intent are
  unambiguous.
- **General Fixture / Useless / Empty / Dependent / Mystery Guest /
  Sensitive Equality: NO.**

### `llm_b/edited/BookScanIntegrationTest.java` (Gemini 3.1 Pro, JUnit 5, 129 lines)

- **Assertion Roulette: NO.** All 21 Jupiter assertions take a message.
- **Eager Test: MILD.** Longest test is `req7_edgeCases` with 5
  assertions, one per edge case (empty list, null list, length 0,
  length 10, null line). The smell is technically present but the
  assertions are all logically related (testing edge-case behaviour
  of the same method), so this is *focused* eager test rather than the
  scattered Phase-1 pattern.
- **Duplicated Asserts: NO.**
- **Magic Number Test: PRESENT (13 literals).**
- **Conditional Test Logic: NO.**
- **General Fixture: PRESENT — 1 field.**
  ```java
  private final BookScan scanner = new BookScan();
  ```
  At class scope. JUnit 5 creates a fresh test-class instance per
  `@Test` method by default, so this field is in fact re-initialised
  per test, and `BookScan` is stateless, so **no observable
  cross-test contamination is possible**. The smell is structural
  rather than behavioural.
- **Useless / Empty / Dependent / Mystery Guest / Sensitive
  Equality: NO.**

## Cross-suite comparison

| Smell                       | 5.1 GPT/unmod | 5.2 Gemini/unmod | 5.3 GPT/edited | 5.4 Gemini/edited |
|---|:-:|:-:|:-:|:-:|
| Assertion Roulette          | – | – | – | – |
| Eager Test                  | mild (6 asserts) | mild (4) | minimal (3) | mild (5) |
| Duplicated Asserts          | mild (Req 1, Req 6) | – | – | – |
| Magic Number Test           | mild (17 lit) | mild (15) | mild (13) | mild (13) |
| Conditional Test Logic      | – | – | mild (1) | – |
| General Fixture             | – | – | – | structural-only (stateless field) |
| Useless / Empty / Dependent | – | – | – | – |
| Mystery Guest / Sensitive Eq| – | – | – | – |

## Per-LLM smell signature

- **GPT-5.5** writes `main`-style suites with custom `check` /
  `assertWordInfo` / `assertListEquals` helpers, descriptive failure
  messages on every assertion, and slightly more lines per suite
  (188 / 192 vs 146 / 129). It is the only LLM that introduces
  duplicated asserts (Run 5.1) and conditional test logic (Run 5.3).
  Both are mild and isolated to one method each.
- **Gemini 3.1 Pro** writes JUnit 5 suites with Jupiter assertions,
  descriptive failure messages on every assertion, and shorter
  files. It is the only LLM that introduces a shared `BookScan` field
  (Run 5.4). Because `BookScan` is stateless and JUnit 5 reconstructs
  the test class per `@Test`, this is structural only.

Neither LLM exhibits Assertion Roulette, Useless Test, Empty Test,
Mystery Guest, Sensitive Equality, or Dependent Test. The two model
signatures differ in **idiom** but converge on the same coarse smell
profile.

## Per-prompt-variant smell signature

- **Unmodified+combined** suites (Runs 5.1 + 5.2) have **50 total
  asserts** with 0 missing messages.
- **Edited+combined** suites (Runs 5.3 + 5.4) have **31 total asserts**
  with 0 missing messages — fewer asserts per suite because the
  helpers (`assertMapEquals`, Jupiter `assertEquals` on whole maps)
  fold what would otherwise be many per-cell checks into one.
- The edited prompt's tighter contract therefore *reduces* the
  surface area where smells could appear, but it does not introduce
  any new smells.

## Comparison with Phase 1

| Cohort | Suites | Assertion-Roulette candidates | Eager-Test candidates | Magic-number issues |
|---|---|---|---|---|
| **Phase 1 base tests**             | 30 | **30 / 30** (all use unlabeled `AssertionError`) | **30 / 30** (each `main` aggregates 5–7 unrelated scenarios) | high |
| Phase 1 improved (`ImprovedBaseTest`) | 30 | 0 (added checks use labelled `check(condition, message)`) | mild | low |
| **Phase 2 integration suites**     | 4 | **0 / 4** | mild only (focused multi-assert per requirement) | mild |

**The Phase 2 integration suites carry a far cleaner smell signature
than Phase 1's base tests, and a slightly cleaner one than Phase 1's
improved tests.** The Step 5 prompt template's explicit instruction
*"each test must include a failure message that names which
requirement failed"* is the dominant explanation: it pre-empted the
single most common smell in Phase 1.

This validates the literature finding (Grano et al. 2024, Phase 1
report §IV.E) that LLM-generated tests carry a recognisable smell
signature that **responds strongly to prompt content**. When the
prompt says "label every assertion," every assertion gets labelled.

## Re-running the analysis

```
python bookscan/analyze_test_smells.py > bookscan/reports/test_smell_metrics.json
```

The `analyze_test_smells.py` source is the authoritative definition
of every metric used in this document.
