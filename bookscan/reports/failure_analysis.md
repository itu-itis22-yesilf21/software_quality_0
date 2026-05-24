# Phase 2 / Step 12 — Failure Analysis: Integration Defects in Complex Classes

This document is the source the report's "problems in integration tests
of complex classes" subsection pulls from. It consolidates every
distinct integration-failure mode observed in Phase 2 with a per-mode
root-cause analysis: what input triggers it, what the brief expects,
what the variant actually does, which helper-method interaction is
responsible, and which Phase 1 literature claim it reinforces.

## What counts as a failure here

We treat a behaviour as an *integration failure* (as opposed to a
unit-test failure) when:

1. The three HumanEval helpers (`howManyTimes`, `strlen`, `flipCase`)
   each pass their own dataset assertions in isolation — confirmed for
   every variant by Step 4's smoke run (4 / 4 variants × 6 / 6 helper
   assertions pass).
2. The public integration method that composes those helpers
   nevertheless produces a result that disagrees with the brief on a
   specific input.

The bugs are therefore in the **glue logic** between the helpers, not
in the helpers themselves. That is the exact failure mode the
assignment brief asks about:

> *"Please evaluate the success of the model in integration test
> scenarios by analyzing in detail the problems that occur in
> integration tests of complex classes."*

## Failure index

Six distinct integration-failure modes are recorded across the four
variants. The three "hard" failures (rows F1–F3) are the spec-aligned
integration assertions that *actually fail at runtime* in Step 6. The
three "soft" divergences (rows D1–D3) are integration behaviours that
disagree with the brief but pass their own variant's regression-style
test, so they only surface in the Step 9 SPEC-DIVERGENCE inventory.

| # | Mode | Variant(s) | Surfaced by | Root cause category |
|---|---|---|---|---|
| **F1** | MixedCase miss in case-folding integration       | `llm_b/unmodified` | Step 6 (Req 1 fails: `expected:<3> but was:<2>`) | Case-folding done with the wrong primitive (`+` instead of `OR over case-equivalence classes`) |
| **F2** | Substring-of-longer-word false positive (`cat` ⊂ `concatenate`) | `llm_b/unmodified` | Step 6 (Req 5 fails: `expected:<1> but was:<2>`) | `howManyTimes` called on raw line text with no word-boundary delimiter |
| **F3** | Substring-of-longer-word false positive (`a` ⊂ `have`)         | `llm_b/unmodified` | Step 6 (Req 6 fails: `expected:<1> but was:<2>`) | **Same root cause as F2** — only the triggering input differs |
| **D1** | No case-folding at all (`The`, `the`, `THE` separate keys) | `llm_a/unmodified` | Step 9 SPEC-DIVERGENCE (variant's own suite ratifies the behaviour, so Step 6 sees no failure) | Normalisation pipeline is the identity function — `flipCase(flipCase(x)) == x` |
| **D2** | Per-line deduplicated `lines` list (`[1]` instead of `[1,1,1,1]`) | Both unmodified variants | Step 9 SPEC-DIVERGENCE | `lines.contains(n)` short-circuit collapses per-occurrence repetition into per-line presence |
| **D3** | Digits and non-ASCII letters treated as word characters | `llm_a/unmodified` only | Step 9 SPEC-DIVERGENCE | Tokeniser regex `[^\p{L}\p{N}]+` is broader than the brief's `[A-Za-z]` definition of "word" |

The two edited variants (`llm_a/edited`, `llm_b/edited`) exhibit
**zero** failure modes from this list. They are recorded here only as
the comparison baseline.

---

## F1 — MixedCase miss in the case-folding integration (Gemini, unmodified)

**Failing test.** `BookScanIntegrationTest.testTokenisationCaseFoldingMatching`
(`bookscan/llm_b/unmodified/BookScanIntegrationTest.java:11–32`).

**Input.**
```text
"The first line\nthe second line\nTHE third line"
```
target length = 3.

**Spec expectation.**
`result.get("the").getTotalOccurrences() == 3` and
`getLines() == [1, 2, 3]`. The same word appears once per line; a
case-folded count should sum to 3.

**Observed output.** `getTotalOccurrences() == 2`, `getLines() == [2, 3]`.
Line 1's `"The"` contributes **0** instead of 1.

**Trace through the helpers.** In
`bookscan/llm_b/unmodified/BookScan.java:78–116`:

1. Line 1's tokens are `["The", "first", "line"]`. The length-3 word
   `"The"` is lower-cased via the **built-in** `.toLowerCase()` (line
   86) — *not* via `flipCase`. The map key therefore becomes `"the"`.
2. For the candidate key `"the"`, the variant computes
   `flippedWord = flipCase("the") == "THE"` (line 94), then for each
   line:
   ```java
   int standardCount = howManyTimes(line, word);       // line 100
   int flippedCount  = howManyTimes(line, flippedWord);
   int totalLineOccurrences = standardCount + flippedCount;
   ```
3. On line 1, `howManyTimes("The first line", "the")` is **0** because
   the string `"the"` (lowercase) does not appear anywhere in
   `"The first line"`. `howManyTimes("The first line", "THE")` is also
   **0**. Line 1 therefore contributes nothing.

**Root cause.** The integration of `flipCase` with `howManyTimes`
treats *case-folded matching* as the **union of two literal searches**
(all-lowercase and all-uppercase). The actual mathematical structure
is the union over **every case-equivalence class of the word's letters**
(`{the, The, tHe, thE, THe, tHE, ThE, THE}` for a 3-letter word — 8
classes). The two literal searches cover only 2 of the 8 classes, so
MixedCase variants like `"The"` are silently dropped.

**Why this is an integration failure.** `flipCase("The")` correctly
returns `"hELLO"`-style flipped output, and `howManyTimes` correctly
counts substring occurrences. The bug is in the **composition rule**:
the model wrote `count_total = count(line, word) + count(line, flipCase(word))`
where the spec needed either (a) a single search over a normalised
line (e.g., lowercase both the line and the word, then count once) or
(b) a search over the full case-equivalence class. Neither helper is
wrong in isolation; the **glue** is.

**How the edited prompt prevented it.** Variant 2 pinned
*"normalise both the candidate word and any stored key by applying
`flipCase` consistently … two words that differ only in case are the
SAME key."* Both edited variants implement `normalize(line)` once and
search the normalised line for the normalised word, avoiding the
two-literal-searches anti-pattern. Gemini's Step 5 integration test
even calls out the failure as deliberate (*"Note: This will correctly
expose the bug where flipCase and howManyTimes fail on 'The'"*) —
recorded in `bookscan/logs/integration_test_generation_log.md`.

**Literature parallel.** Siddiq et al. 2024 — LLM-generated tests
cluster around prompt examples. The unmodified prompt's HumanEval
examples for `flipCase` show `"Hello" → "hELLO"` — a single MixedCase
input. The model extrapolated to "case-folded means lowercase OR
uppercase" rather than "case-folded means all case-equivalence
classes," because the prompt's single example did not constrain the
generalisation.

---

## F2 — Substring-of-longer-word false positive (`cat` ⊂ `concatenate`)

**Failing test.** `BookScanIntegrationTest.testHowManyTimesSubstringOfLongerWord`
(`bookscan/llm_b/unmodified/BookScanIntegrationTest.java:91–104`).

**Input.** `"cat concatenate"`, target length = 3.

**Spec expectation.** `result.get("cat").getTotalOccurrences() == 1`.

**Observed output.** `getTotalOccurrences() == 2`.

**Trace.** In `bookscan/llm_b/unmodified/BookScan.java:78–116`:

1. The tokeniser `line.split("[^a-zA-Z]+")` correctly identifies
   `"cat"` and `"concatenate"` as the line's two tokens.
2. The candidate key for length 3 is `"cat"` (lowercased).
3. For the line `"cat concatenate"`, the integration code calls
   `howManyTimes("cat concatenate", "cat")` (line 100). This is a
   raw substring count: it matches at position 0 (the standalone
   `"cat"`) *and* at position 4 (inside `"concatenate"`), so it
   returns **2**. The `flipCase` variant returns 0. `2 + 0 = 2`.

**Root cause.** `howManyTimes` is a substring counter, not a
whole-word counter. When the integration method calls it on the raw
line text without first wrapping each token in a word-boundary
delimiter, every occurrence of the target string inside any longer
word is counted as a hit. The bug is the *interface contract* between
the tokeniser and `howManyTimes`: the tokeniser knows that `"cat"`
and `"concatenate"` are distinct, but `howManyTimes` does not see that
context.

**Why this is an integration failure.** `howManyTimes("aaaa", "aa")`
correctly returns 3 — the helper does exactly what its Javadoc says.
The integration method handed it raw line text, when it should have
either (a) iterated the tokeniser's output and `equals`-matched each
token, or (b) joined the tokens with a sentinel character that cannot
appear in a word (the `` delimiter both edited variants chose)
before searching.

**How the edited prompt prevented it.** Variant 2 included the
explicit rule *"When you delimit a normalised line for `howManyTimes`,
ensure that searching for the substring 'the' inside 'thethem' does
NOT match twice — use a delimiter character that cannot appear in a
word (e.g. wrap each token with ``)."* Both edited variants
implemented this almost verbatim. The clause is the most surgical
single edit in the entire Variant 2 prompt — it eliminates this class
of bug categorically.

**Literature parallel.** TELPA (Yang et al. 2024) — hard-to-cover
branches that depend on *inter-procedural state*. Here the
"inter-procedural state" is the implicit invariant *"howManyTimes is
called only on text where every word is followed by a delimiter."*
Neither model invents this invariant unprompted.

---

## F3 — Substring trap, second incarnation (`a` ⊂ `have`)

**Failing test.** `BookScanIntegrationTest.testStrlenEmptyOneCharacterWords`
(`bookscan/llm_b/unmodified/BookScanIntegrationTest.java:107–120`).

**Input.** `"I have a pen"`, target length = 1.

**Spec expectation.** `result.get("a").getTotalOccurrences() == 1`.

**Observed output.** `getTotalOccurrences() == 2`.

**Trace.** The tokeniser correctly identifies `["I", "have", "a", "pen"]`.
The length-1 candidate keys are `"i"` and `"a"`. For `"a"`, the
integration calls `howManyTimes("I have a pen", "a")`. The substring
`"a"` matches *inside* `"have"` (position 3) and as the standalone
word (position 7), returning **2**.

**Root cause.** **Identical to F2** — `howManyTimes` searches the raw
line text. We list F3 separately because it exposes the **same bug
at a different `wordLength`** and through a different word, which
matters for the report's argument:

- Step 5's prediction was *"only F1 and F2 will fail."*
- Step 6's actual result was *"F1, F2, and F3 fail."*
- The third failure was not predicted because we assumed Req 6's
  one-character test text would not happen to embed the target word
  inside a longer word. It did (`"a"` is inside `"have"`).
- The bug is therefore not a single-input curiosity; it surfaces in
  *two of the seven* integration tests Gemini wrote, on inputs of
  very different shapes. This is much stronger evidence than a single
  failure would have been.

The structural similarity of F2 and F3 is the key data point: a
single root-cause bug in the integration glue produces an unbounded
family of failing inputs, and we can only know the family is unbounded
because two independently-authored test scenarios both hit it.

**How the edited prompt prevented it.** Same ``-delimiter rule
that fixed F2; both edited variants pass the analogous tests in
their `BookScanBlackBoxTest.java` (Step 9).

---

## D1 — Identity-function "normalisation" (GPT, unmodified)

**Variant.** `llm_a/unmodified` only.

**Symptom.** Mixed-case occurrences of the same word produce
**separate keys**:
```text
scanWords("The cat sat on the mat\nOn the MAT was a rat\n...", 3)
  → {The=count=1, lines=[1], the=count=7, lines=[1,2,3,4,5],
     MAT=count=1, lines=[2], mat=count=1, lines=[1], ...}
```
Recorded in `bookscan/reports/smoke_results.md` (Step 4) and
`bookscan/reports/black_box_test_results.md` SPEC-DIVERGENCE row
"V6 / V11 case + lines."

**Why this is a soft divergence rather than a Step-6 hard failure.**
GPT-5.5's Step 5 integration test (Run 5.1) *ratifies* this behaviour
— Req 1 explicitly asserts that `"The"`, `"the"`, and `"THE"` should
each appear as a separate key with `count=1` and lines `[1]`, `[2]`,
`[3]` respectively. The test passes; the spec disagrees.

**Trace.** `bookscan/llm_a/unmodified/BookScan.java:121`:
```java
String word = flipCase(flipCase(rawWord));
```
`flipCase` is an involution (applying it twice returns the original
string). The model wrote a normalisation pipeline whose every step is
exactly cancelled, so no normalisation actually occurs.

**Root cause.** Mis-binding of the helper's role. `flipCase` is *not*
a case-normaliser; it is an idempotent-when-double-applied case-flipper.
A correct normaliser uses `flipCase` *conditionally* (only when the
character is upper-case) so that the cumulative effect is "everything
ends up lowercase." Both edited variants implemented exactly that
conditional logic (see `bookscan/llm_a/edited/BookScan.java:136–150`
`normalise()` and `bookscan/llm_b/edited/BookScan.java:125–136`
`normalizeToLowercase()`).

**Why this is an integration failure rather than a helper-spec
problem.** `flipCase("Hello") == "hELLO"` is the spec, and it holds
for GPT's `flipCase` in isolation. The integration method *called*
the helper twice — equivalent to not calling it at all. The helper
spec is fine; the calling pattern is the bug.

**Literature parallel.** HITS (Wang et al. 2024) — when an LLM is
asked one big question, it gives one big answer. The unmodified
prompt asked GPT to "use flipCase, strlen, howManyTimes" without
specifying *what role* each helper plays. The model satisfied the
"use" requirement literally — `flipCase` is called twice — without
the called pattern doing meaningful work.

---

## D2 — Per-line deduplication of the `lines` list

**Variants.** Both unmodified variants exhibit this; both edited
variants do not.

**Symptom.** A word appearing N times on line K produces `lines = [K]`
rather than `lines = [K, K, …, K]` (N entries). Step 4 smoke output:

| Variant | Behaviour on `"the the the"` line |
|---|---|
| `llm_a/unmodified` | `lines = [1]`, `count = 3` (count recovered separately by a second `howManyTimes` pass) |
| `llm_b/unmodified` | `lines = [1]`, `count = 3` (count accumulated per-line) |
| `llm_a/edited` | `lines = [1, 1, 1]`, count implicit in list length |
| `llm_b/edited` | `lines = [1, 1, 1]`, count implicit in list length |

**Trace.**
- `bookscan/llm_a/unmodified/BookScan.java:89–91`:
  ```java
  if (!lines.contains(lineNumber)) {
      lines.add(lineNumber);
  }
  ```
- `bookscan/llm_b/unmodified/BookScan.java:134–138`:
  ```java
  public void addLine(int lineNumber) {
      if (!this.lines.contains(lineNumber)) {
          this.lines.add(lineNumber);
      }
  }
  ```

Both LLMs independently introduced a `lines.contains(n)` short-circuit.

**Root cause.** The brief is **ambiguous** on this point — it says
*"determine how many times words of a given length appear in a text
and in which lines they appear,"* which can be read as either
*"return the set of lines containing the word"* or *"return one entry
per occurrence."* The unmodified prompt did not pin the answer, so
both LLMs chose the set interpretation. The edited prompt pinned the
per-occurrence interpretation explicitly:

> *"Map values are the 1-based line numbers in which the word appears,
> in ascending order, with one entry per occurrence (a word that
> appears twice on line 4 yields `[4, 4]`)."*

This is therefore not a "bug" so much as evidence that **ambiguous
specs do not converge on a single interpretation across LLMs** — both
chose the same wrong (relative to the brief's intent) interpretation,
but only because the simpler answer was the same for both. A different
underspecification might have split them.

**Why we list it as an integration failure.** Even though the bug is
not in either helper, it materially affects the integration method's
return shape and would silently break any downstream code that
expected per-occurrence semantics.

---

## D3 — Digits and non-ASCII letters as word characters (GPT, unmodified)

**Variant.** `llm_a/unmodified` only.

**Symptom.**
- `scanWords("abc 123 def", 3)` returns `{abc, 123, def}` — the
  digit-only token `"123"` is treated as a length-3 word.
- `scanWords("Yeşil tree", 5)` returns `{Yeşil}` — the non-ASCII
  Turkish word `Yeşil` is treated as a single length-5 word.

Both behaviours were verified in `bookscan/llm_a/unmodified/BookScanBlackBoxTest.java`
(Step 9 V12 / I4 checks).

**Trace.** `bookscan/llm_a/unmodified/BookScan.java:113`:
```java
String[] words = line.split("[^\\p{L}\\p{N}]+");
```
The `\p{L}` Unicode property matches *any* letter (including
non-ASCII Turkish letters), and `\p{N}` matches *any* digit. The brief
defines a word as `[A-Za-z]` only.

**Root cause.** The model defaulted to the "permissive Unicode-aware"
tokenisation idiom that is common in Java tutorials, rather than the
narrower spec the brief implied. Gemini's unmodified variant chose
`[^a-zA-Z]+` (ASCII letters only, no digits) and is therefore closer
to the brief here despite being further from the brief on F1/F2.

**Why this is an integration failure rather than a tokenisation
preference.** Once `"123"` is in the result map, downstream callers
(e.g., a UI that lists "words found") will display numeric strings as
if they were vocabulary, which silently breaks the integration's
implicit contract. The spec was non-binding, so both choices "work";
the report's job is to record that the unmodified prompt left the
choice to the model, which made two different choices on two
different LLMs.

**How the edited prompt prevented it.** Variant 2 stated:
*"A 'word' inside a line is a maximal run of letters `[A-Za-z]`
separated by any non-letter character"* — explicit, unambiguous, and
both edited variants implemented exactly this.

---

## Cross-failure pattern analysis

The six failure modes consolidate to **three integration anti-patterns**:

| Anti-pattern | Failure modes | Why it appeared |
|---|---|---|
| **Wrong helper composition rule** | F1, D1 | The model satisfied the *"use helper X"* requirement literally but composed the helpers in a pattern that does not implement the brief's intended semantics. F1 sums two literal searches; D1 cancels two `flipCase` calls. |
| **Missing word-boundary invariant** | F2, F3 | `howManyTimes` is a substring counter. Without a delimiter discipline at the call site, every occurrence of the target inside a longer word is counted. This single bug surfaces in two independent failing tests, demonstrating the *family* of inputs the bug affects. |
| **Underspecified contract → divergent choices** | D2, D3 | The brief left a detail ambiguous, both LLMs (or one of them) filled in a non-spec-aligned default. D2 occurs in both unmodified variants; D3 in only one. The edited prompt eliminated both by pinning the contract. |

## Per-LLM pattern

- **GPT-5.5 (`llm_a/unmodified`).** Two distinct anti-pattern failures:
  *Wrong helper composition rule* (D1) and *Underspecified contract*
  (D3). Both are visible only via SPEC-DIVERGENCE because GPT's own
  Step 5 integration test ratifies them.
- **Gemini 3.1 Pro (`llm_b/unmodified`).** Three distinct anti-pattern
  failures: *Wrong helper composition rule* (F1), *Missing
  word-boundary invariant* (F2, F3), *Underspecified contract* (D2).
  Two of the three surface as real Step-6 test failures because
  Gemini's own integration suite asserted the spec.

The number of *anti-patterns* per LLM is similar (2 vs 3). The
*observable* failure rate differs because GPT's regression-style
suite hides the divergences; Gemini's aspirational suite exposes
them. This is the central per-LLM observation for the report:
**different testing styles produce different observable failure
rates even when the underlying integration defect count is
comparable.**

## Per-prompt-variant pattern

| Prompt | Anti-patterns observed | Hard failures (Step 6) | Soft divergences (Step 9) |
|---|:-:|:-:|:-:|
| unmodified+combined (both LLMs) | 3 | 3 | 3 |
| edited+combined     (both LLMs) | 0 | 0 | 0 |

Every anti-pattern observed in Phase 2 is concentrated in the two
unmodified variants. Both edited variants have **zero** anti-patterns.
This is the strongest single piece of evidence in Phase 2 that the
edited prompt's explicit contract eliminates the integration-failure
class entirely.

## What the report's "complex-class integration failures" subsection should say

Direct usable prose, ready to drop into `report.tex` §VI under the
"Problems in integration tests of complex classes" heading:

> The Phase 2 BookScan class exposed two qualitatively different
> failure modes that no Phase 1 task could have surfaced. First, the
> *wrong helper composition rule* anti-pattern: both unmodified
> variants called `flipCase` correctly in isolation, but composed it
> with `howManyTimes` in a pattern that does not implement
> case-folded matching (GPT cancels two flips into a no-op; Gemini
> sums two literal searches and misses MixedCase variants). Second,
> the *missing word-boundary invariant*: Gemini's unmodified variant
> calls `howManyTimes` on raw line text, so the target word `"cat"`
> matches inside `"concatenate"` and the target `"a"` matches inside
> `"have"`. A single integration defect therefore surfaces in two
> independently-authored test cases at very different word lengths,
> which would not happen with a single-method HumanEval task. The
> edited prompt eliminated both classes categorically by (a) pinning
> `flipCase` to a normalisation role and (b) requiring delimiter-
> wrapped `howManyTimes` calls — both edited variants produced
> implementations that pass every Step-6 integration assertion and
> every Step-9 mutation assertion.

## Cross-references

- Step 4 smoke output per variant: `bookscan/reports/smoke_results.md`
- Step 6 raw test results: `bookscan/reports/integration_test_results.json/.md`
- Step 9 SPEC-DIVERGENCE inventory: `bookscan/reports/black_box_test_results.md`
- Step 11 comparison data + charts: `bookscan/reports/comparison.md`,
  `report/figures/phase2_comparison.png`,
  `report/figures/phase2_prompt_effect.png`
- Per-LLM Step-3 design choices: `bookscan/logs/class_generation_log.md`
- Per-LLM Step-5 testing style: `bookscan/logs/integration_test_generation_log.md`
