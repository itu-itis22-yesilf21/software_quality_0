# Phase 2 / Step 5 — Integration Test Prompt Design

This file holds the four integration-test generation prompts that will
be sent to the agents in Step 5. The design rule is: **each LLM
receives the BookScan.java it produced**, so the integration tests
target the API actually built, not an abstract spec. That keeps the
"did the agent generate good integration tests?" question separable
from the "did the agent obey the spec?" question we already answered
in Step 3.

The same template (below) is instantiated four times, with only the
inlined `BookScan.java` source changing. That parallel structure lets
the Step 11 comparison table say something honest: "given identical
test-generation instructions and only the BookScan source as a
variable, how do the four resulting test suites compare?"

---

## Shared template (will be instantiated per variant)

```text
You will write a single Java file called BookScanIntegrationTest.java
for a course project on software quality and testing.

The class under test is the BookScan class shown at the bottom of this
prompt. Your job is to write **integration tests** — tests that
exercise the *interactions* between the three required helper methods
(howManyTimes, strlen, flipCase) and the public integration method. Do
NOT write unit tests that exercise the helpers in isolation; we
already have those.

Concrete coverage requirements (the test suite MUST cover all of these
interactions):

1. **Tokenisation × case-folding × matching**: a multi-line text in
   which the same word appears in mixed case ("The", "the", "THE")
   across different lines, with the integration method asked to find
   words of a length that includes that word. Assert what the public
   integration method returns for that word.

2. **Tokenisation × length filtering**: a text containing words of
   several different lengths, with words of the target length
   scattered among them. Assert that only words of the target length
   appear in the result.

3. **Tokenisation × repeated-word counting**: a line in which the same
   target-length word appears more than once. Assert how the
   integration method represents the repeated occurrence (per-occurrence
   list, per-line count, or whatever shape this BookScan returns).

4. **flipCase × howManyTimes**: an input where case-insensitivity is
   the *only* way to get the expected count. Show that turning off
   case-folding (if exposed) or feeding only one case variant gives
   the wrong answer.

5. **howManyTimes × substring-of-longer-word**: a text containing a
   short target word that is *also* a substring of a longer word on
   the same line (e.g., target length 3, line contains "cat
   concatenate"). Assert that the integration method counts the short
   word exactly once on that line, not twice.

6. **strlen × empty / one-character words**: a text with one-character
   "words" (e.g., "I a") and a target length of 1. Assert what the
   integration method returns.

7. **Edge cases**: empty input (empty list or empty string, whichever
   the integration method accepts); target length 0; target length
   that exceeds every word in the text.

For each test, write one method (or one labelled `check` call) with a
descriptive name that maps clearly to one of the seven requirements
above. Each test must include a failure message that names which
requirement failed.

You may use JUnit 5 (`@Test` + `org.junit.jupiter.api.Assertions`) if
you prefer; otherwise write a `public static void main(String[] args)`
driver in the Phase 1 style that throws `AssertionError(message)` on
failure. Choose one of the two; do not mix.

Constraints:

- Java 11 source compatibility. Only `java.util` and `java.lang`
  imports beyond the test framework imports.
- Single Java file, single class named `BookScanIntegrationTest`.
- Add the standard header at the very top of the file:

  /*@Authors
  Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
  Student IDs:<150210062><10210041><150210059>*/

- Output ONLY the contents of BookScanIntegrationTest.java in a single
  fenced code block. No prose.

============================================================
BookScan class under test:
============================================================

<INLINE THE FULL BookScan.java SOURCE HERE>
```

---

## Per-variant instantiation

When constructing each prompt, the operator copies the template above
verbatim and pastes the variant's full `BookScan.java` source where
the `<INLINE THE FULL BookScan.java SOURCE HERE>` marker is, then sends
the assembled prompt to the corresponding LLM:

| Run | LLM             | Variant      | Source path                                  | Save test to                                              |
|-----|-----------------|--------------|----------------------------------------------|-----------------------------------------------------------|
| 5.1 | GPT-5.5         | unmodified+combined | `bookscan/llm_a/unmodified/BookScan.java`     | `bookscan/llm_a/unmodified/BookScanIntegrationTest.java`  |
| 5.2 | Gemini 3.1 Pro  | unmodified+combined | `bookscan/llm_b/unmodified/BookScan.java`     | `bookscan/llm_b/unmodified/BookScanIntegrationTest.java`  |
| 5.3 | GPT-5.5         | edited+combined     | `bookscan/llm_a/edited/BookScan.java`         | `bookscan/llm_a/edited/BookScanIntegrationTest.java`      |
| 5.4 | Gemini 3.1 Pro  | edited+combined     | `bookscan/llm_b/edited/BookScan.java`         | `bookscan/llm_b/edited/BookScanIntegrationTest.java`      |

The full prompt + full response + use-note for each run is recorded in
`bookscan/logs/integration_test_generation_log.md` (skeleton created
in Step 5 setup).

---

## Why we keep the prompt identical across variants

The Phase 2 brief says we must "verify the effectiveness of the tests
produced by the agent using the same approaches in Phase 1." Holding
the prompt fixed and varying only the source is the cleanest way to
attribute Step 11 differences to either (a) the LLM, (b) the source
quality (edited vs unmodified), or (c) their interaction. Anything
else would confound the comparison.

## Out-of-scope for this step

- We are NOT regenerating BookScan in this step (Step 10's refactoring
  loop is the only place that ever regenerates code).
- We are NOT writing the tests by hand. The agent writes them; we save
  them verbatim, then evaluate them in Steps 6–9.
- We are NOT modifying the integration test responses after they arrive
  (other than restoring obvious paste artefacts as we did for Run 1 in
  Step 3, with the same documentation rule).
