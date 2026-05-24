# Phase 2 / Step 9 — Black-Box ECP / BVA Table for `BookScan`

Mirrors the Phase 1 methodology in `black_box_equivalence_analysis.md`.
We enumerate **valid** equivalence classes, **invalid / out-of-contract**
classes, and **boundary conditions** for the BookScan integration
contract as defined by the assignment brief:

> "Determine how many times words of a given length appear in a text
> and in which lines they appear."

Note: the four variants ship four different concrete contracts (Step 3
analysis). For ECP scoring we use the **brief-level** contract — i.e.,
the canonical interpretation pinned by Variant 2 — because that is the
only ground truth that lets the four variants be compared on the same
yardstick.

Invalid classes are listed as out-of-contract when the brief is silent
on them. Following Phase 1, they are not asserted as hard
requirements; instead they are recorded so the report can show which
classes are exercised and which are deferred.

## ECP / BVA table

| ID  | Class type      | Equivalence class / boundary                                                                 | Why it matters |
|-----|-----------------|----------------------------------------------------------------------------------------------|---|
| V1  | Valid           | Empty text (empty list / empty string)                                                       | Empty-input handling — Phase 1 "empty / singleton inputs" mutation family |
| V2  | Valid           | Single-line text with one matching word                                                      | Minimal positive case |
| V3  | Valid           | Multi-line text with one matching word per line                                              | Line-number plumbing |
| V4  | Valid           | Same word repeated on the same line (per-occurrence behaviour)                               | Counting semantics |
| V5  | Valid           | Same word in **non-consecutive** lines (e.g., L1 and L5 only)                                | Line-number ordering across gaps |
| V6  | Valid           | Same word in mixed case (`The` / `the` / `THE`)                                              | Case-folding behaviour |
| V7  | Valid           | Target-length word that is also a substring of a longer word on the same line                | Word-boundary correctness |
| V8  | Valid           | Words separated by various punctuation (`,`, `.`, `;`, `:`, `!`, `?`)                        | Tokenisation breadth |
| V9  | Valid           | Words separated by tabs / multiple spaces                                                    | Whitespace tokenisation |
| V10 | Valid           | One-character words (target length 1)                                                        | `strlen` × tokenisation interaction at the smallest non-zero length |
| V11 | Valid           | Text containing **only** the target word repeated (e.g., `"the the the"`)                    | Per-occurrence saturation |
| V12 | Valid           | Text containing digits and letters mixed (e.g., `"abc 123 def"`)                             | Non-letter tokens not counted as words |
| B1  | Boundary        | `wordLength = 0`                                                                             | Lower exclusive boundary (spec: empty result) |
| B2  | Boundary        | `wordLength = 1`                                                                             | Smallest matching length |
| B3  | Boundary        | `wordLength` larger than every word in the text                                              | Upper "no-match" boundary |
| B4  | Boundary        | `wordLength = strlen(longestWord)`                                                           | Inclusive upper match boundary |
| B5  | Boundary        | Single line containing exactly one word                                                      | Minimum positive boundary |
| B6  | Boundary        | Empty line in the middle of the text                                                         | Empty-element-within-list handling |
| B7  | Boundary        | Trailing / leading blank lines                                                               | Off-by-one line numbering |
| B8  | Boundary        | Whitespace-only line (only spaces / tabs)                                                    | Tokenisation produces no words on this line |
| B9  | Boundary        | Word with apostrophe or hyphen between letters (`don't`, `x-ray`)                            | Spec says `'` and `-` are NOT word chars → must split |
| B10 | Boundary        | Same case-variant word on the boundary line numbers (L1 + last line)                         | Line-number ordering and saturation |
| I1  | Invalid / OOC   | `wordLength = -1` (negative)                                                                 | Spec: empty result (edited); unmodified variants make their own choice |
| I2  | Invalid / OOC   | `null` text / `null` list                                                                    | Spec: empty result (edited) |
| I3  | Invalid / OOC   | `null` element inside the list                                                               | Spec: treat as empty line (edited only) |
| I4  | Invalid / OOC   | Non-ASCII letters (e.g., `Yeşil`, `Çavdar`)                                                  | Spec is silent — implementation-defined |

## Per-variant integration-suite coverage of the table

We score each variant's `BookScanIntegrationTest.java` (Step 5) by
counting how many of the 22 classes above the existing suite asserts
against. The same Phase 1 rubric (`base_test_effectiveness_assessment.md`)
applies:

| Rating | Criterion |
|---|---|
| **High**   | ≥ 80 % of identified equivalence classes and boundaries asserted |
| **Medium** | 50 % – 79 % |
| **Low**    | < 50 % |

We score valid + boundary classes (`V1..V12` + `B1..B10` = 22 classes).
Invalid / out-of-contract classes are reported separately because the
brief does not pin behaviour for them.

### Run 5.1 — `llm_a/unmodified` (GPT-5.5)

| Class | Asserted by suite? | Where |
|---|:-:|---|
| V1  Empty text                              | ✓ | Req 7 (`scanWords("", 3)` → empty) |
| V2  Single-line, single match               | – | implicit only |
| V3  Multi-line, one match per line          | ✓ | Req 2 (cat / dog / sun / ant across 3 lines) |
| V4  Repeated on same line                   | ✓ | Req 3 (`"cat dog cat cat"`) |
| V5  Non-consecutive lines                   | – | |
| V6  Mixed-case same word                    | ✓ | Req 1 — but baked in as 3 separate keys (the *bug*) |
| V7  Substring of longer word                | ✓ | Req 5 (`"cat concatenate dog"`) |
| V8  Mixed punctuation                       | – | |
| V9  Tabs / multiple spaces                  | – | |
| V10 One-character words                     | ✓ | Req 6 |
| V11 Target word repeated only               | – | |
| V12 Digits + letters                        | – | |
| B1  wordLength = 0                          | ✓ | Req 7 |
| B2  wordLength = 1                          | ✓ | Req 6 |
| B3  wordLength larger than every word       | ✓ | Req 7 |
| B4  wordLength = longest                    | – | |
| B5  Single line, single word                | – | |
| B6  Empty line in middle                    | – | |
| B7  Trailing / leading blank lines          | – | |
| B8  Whitespace-only line                    | – | |
| B9  Apostrophe / hyphen                     | – | |
| B10 First + last line                       | – | |
| **Asserted**                                | **10 / 22 (45 %)** | |
| **Rating**                                  | **Low** | |

### Run 5.2 — `llm_b/unmodified` (Gemini 3.1 Pro)

| Class | Asserted by suite? | Where |
|---|:-:|---|
| V1  Empty text                              | ✓ | Req 7 (`""` and `null`) |
| V2  Single-line, single match               | – | |
| V3  Multi-line, one match per line          | ✓ | Req 1 (3 lines) |
| V4  Repeated on same line                   | ✓ | Req 3 (`"apple banana apple orange apple"`) |
| V5  Non-consecutive lines                   | – | |
| V6  Mixed-case same word                    | ✓ | Req 1 — asserts spec (3 occurrences), fails on the bug |
| V7  Substring of longer word                | ✓ | Req 5 (`"cat concatenate"`) |
| V8  Mixed punctuation                       | – | |
| V9  Tabs / multiple spaces                  | – | |
| V10 One-character words                     | ✓ | Req 6 (`"I have a pen"` length 1) |
| V11 Target word repeated only               | – | |
| V12 Digits + letters                        | – | |
| B1  wordLength = 0                          | ✓ | Req 7 |
| B2  wordLength = 1                          | ✓ | Req 6 |
| B3  wordLength larger than every word       | ✓ | Req 7 (length 50) |
| B4  wordLength = longest                    | – | |
| B5  Single line, single word                | – | |
| B6  Empty line in middle                    | – | |
| B7  Trailing / leading blank lines          | – | |
| B8  Whitespace-only line                    | – | |
| B9  Apostrophe / hyphen                     | – | |
| B10 First + last line                       | – | |
| **Asserted**                                | **10 / 22 (45 %)** | |
| **Rating**                                  | **Low** | |

### Run 5.3 — `llm_a/edited` (GPT-5.5)

| Class | Asserted by suite? | Where |
|---|:-:|---|
| V1  Empty text                              | ✓ | Req 7 (empty list) |
| V2  Single-line, single match               | – | |
| V3  Multi-line, one match per line          | ✓ | Req 1 |
| V4  Repeated on same line                   | ✓ | Req 3 (`"red blue red red green"` → `[1,1,1]`) |
| V5  Non-consecutive lines                   | – | |
| V6  Mixed-case same word                    | ✓ | Req 1 + Req 4 (`DOG/dog/DoG`) |
| V7  Substring of longer word                | ✓ | Req 5 |
| V8  Mixed punctuation                       | – | |
| V9  Tabs / multiple spaces                  | – | |
| V10 One-character words                     | ✓ | Req 6 (`"I a / B see c"` length 1) |
| V11 Target word repeated only               | – | |
| V12 Digits + letters                        | – | |
| B1  wordLength = 0                          | ✓ | Req 7 |
| B2  wordLength = 1                          | ✓ | Req 6 |
| B3  wordLength larger than every word       | ✓ | Req 7 (length 10) |
| B4  wordLength = longest                    | – | |
| B5  Single line, single word                | – | |
| B6  Empty line in middle                    | – | |
| B7  Trailing / leading blank lines          | – | |
| B8  Whitespace-only line                    | – | |
| B9  Apostrophe / hyphen                     | – | |
| B10 First + last line                       | – | |
| **Asserted**                                | **10 / 22 (45 %)** | |
| **Rating**                                  | **Low** | |

### Run 5.4 — `llm_b/edited` (Gemini 3.1 Pro)

| Class | Asserted by suite? | Where |
|---|:-:|---|
| V1  Empty text                              | ✓ | Req 7 (empty list + null list) |
| V2  Single-line, single match               | – | implicit only |
| V3  Multi-line, one match per line          | ✓ | Req 1 (4 lines) |
| V4  Repeated on same line                   | ✓ | Req 3 (`"hello world hello hello"`) |
| V5  Non-consecutive lines                   | ✓ | Req 1 (`the` on L1, L3, L4) |
| V6  Mixed-case same word                    | ✓ | Req 1 + Req 4 (`Cat CAT cAt caT`) |
| V7  Substring of longer word                | ✓ | Req 5 (4 trap variants) |
| V8  Mixed punctuation                       | – | |
| V9  Tabs / multiple spaces                  | – | |
| V10 One-character words                     | ✓ | Req 6 (`"I a m a t"` length 1) |
| V11 Target word repeated only               | – | |
| V12 Digits + letters                        | – | |
| B1  wordLength = 0                          | ✓ | Req 7 |
| B2  wordLength = 1                          | ✓ | Req 6 |
| B3  wordLength larger than every word       | ✓ | Req 7 (length 10) |
| B4  wordLength = longest                    | – | |
| B5  Single line, single word                | – | |
| B6  Empty line in middle                    | – | |
| B7  Trailing / leading blank lines          | – | |
| B8  Whitespace-only line                    | – | |
| B9  Apostrophe / hyphen                     | – | |
| B10 First + last line                       | – | |
| **Asserted**                                | **11 / 22 (50 %)** | |
| **Rating**                                  | **Medium** | |

### Per-suite scoring summary

| Variant            | Asserted | % | Rating |
|---|---:|---:|---|
| `llm_a/unmodified` | 10 / 22 | 45 % | **Low** |
| `llm_b/unmodified` | 10 / 22 | 45 % | **Low** |
| `llm_a/edited`     | 10 / 22 | 45 % | **Low** |
| `llm_b/edited`     | 11 / 22 | 50 % | **Medium** |

All four variants under-cover the brief's equivalence classes. The
Step 5 prompt asked for seven concrete requirements; those seven
requirements collectively touch ~10–11 of the 22 classes here, which
leaves ~11–12 boundary partitions untested. **Step 9's
`BookScanBlackBoxTest.java` mutation suite is therefore generated for
every variant**, not just the worst ones — this mirrors the Phase 1
result (30/30 tasks needed mutation suites).

## Invalid / out-of-contract classes (reported, not scored)

| ID  | Class                            | LLM A unmod  | LLM B unmod  | LLM A edit | LLM B edit |
|-----|----------------------------------|:-:|:-:|:-:|:-:|
| I1  | Negative wordLength              | –  | –  | ✓ (`-1`) | ✓ (`-1`) — implicit in `wordLength<=0` guard |
| I2  | `null` text / list               | –  | ✓ | ✓ | ✓ |
| I3  | `null` element inside list       | –  | –  | ✓ | ✓ (Req 7 `singletonList(null)`) |
| I4  | Non-ASCII letters                | –  | –  | –  | –  |

The two edited variants pre-emptively cover three of the four
out-of-contract classes (because the spec pinned them). The two
unmodified variants cover only what their authors thought to test.

## Mutation taxonomy applied in `BookScanBlackBoxTest.java`

Following Phase 1 (Table II of the report), the mutations in each
variant's `BookScanBlackBoxTest.java` are drawn from these families:

| Family | Examples used here |
|---|---|
| Empty / singleton inputs | `""` / `[]`; single-line single-word text |
| Sign reversal            | `wordLength = -1`, `wordLength = -100` |
| Boundary substitution    | `wordLength = 0`, `wordLength = 1`, `wordLength = strlen(longest)`, `wordLength = strlen(longest) + 1` |
| Sentinel insertion       | line with only the target word repeated; whitespace-only line; blank line at start/middle/end |
| Token / character swap   | apostrophes, hyphens, mixed punctuation, tabs, non-ASCII letters |
| Range edge               | non-consecutive lines (L1 + L5 only); first + last line |

`BookScanBlackBoxTest.java` per variant adapts these to the variant's
actual API. For variants whose **implementation is known to disagree
with the spec** on a class (case sensitivity in `llm_a/unmodified`;
MixedCase miss and substring-in-longer-word in `llm_b/unmodified`),
the mutation suite asserts the *variant's actual behaviour* with a
comment recording the divergence from the spec. That keeps the suite
green (the goal Phase 1 set with "30/30 mutation suites passing")
while preserving the diagnostic value in the comments and the
companion `bookscan/reports/black_box_test_results.md` analysis.
