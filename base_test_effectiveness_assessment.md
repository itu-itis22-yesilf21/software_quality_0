# Base Test Effectiveness Assessment

This assessment evaluates the original `BaseTest.java` files against black-box equivalence class partitioning and boundary-value analysis. A base test is considered effective only when it exercises both the nominal valid classes and the boundary conditions implied by the prompt.

## Scoring Method

| Rating | Criterion |
|---|---|
| High | Base tests cover at least 80% of identified equivalence classes and boundaries. |
| Medium | Base tests cover 50% to 79% of identified equivalence classes and boundaries. |
| Low | Base tests cover less than 50% of identified equivalence classes or boundaries. |

Invalid classes are listed as out-of-contract when the prompt does not define behavior for them. They were not executed as required assertions because doing so would test unspecified behavior rather than the solution contract.

## Overall Result

| Metric | Result |
|---|---:|
| Tasks assessed | 30 |
| High effectiveness base tests | 3 |
| Medium effectiveness base tests | 17 |
| Low effectiveness base tests | 10 |
| Tasks requiring extra black-box mutations | 30 |
| Generated mutation suites passing | 30/30 |

Conclusion: the base tests are useful smoke/regression tests, but they are not sufficient black-box suites. They mostly cover representative examples from the prompt and miss many boundary partitions such as empty inputs, singleton inputs, exact thresholds, zero values, duplicate values, ties, and wraparound cases. The generated `BlackBoxTest.java` files improve this by mutating base-test inputs around the missing partitions.

## Per-Task Effectiveness

| Task | Eq. Classes Covered By Base | Boundaries Covered By Base | Effectiveness | Important Classes / Boundaries Missed By Base | Added Mutation Tests |
|---|---:|---:|---|---|---|
| Java/0 `hasCloseElements` | 3/5 | 1/3 | Medium | Empty/singleton lists; distance exactly equal to threshold; negative-number close pair | Empty list, singleton, exact-threshold false, negative close pair |
| Java/1 `separateParenGroups` | 3/5 | 1/3 | Medium | Empty input; adjacent groups without spaces; leading/trailing spaces | `""`, `"()()"`, `" ((())) "` |
| Java/2 `truncateNumber` | 1/3 | 0/3 | Low | Integer input with decimal part `0`; value between `0` and `1`; large decimal precision | `7.0`, `0.125`, `123456.789` |
| Java/3 `belowZero` | 2/5 | 1/3 | Medium | Empty operations; balance exactly zero; first operation below zero | Empty list, exact-zero sequence, first withdrawal |
| Java/4 `meanAbsoluteDeviation` | 1/4 | 0/3 | Low | Singleton list; identical values; mixed-sign values | Singleton, identical values, `[-1.0, 1.0]` |
| Java/5 `intersperse` | 2/5 | 1/3 | Medium | Singleton list; negative input values; negative delimiter | Singleton, negative values, negative delimiter |
| Java/6 `parseNestedParens` | 3/5 | 1/3 | Medium | Empty string; repeated spaces; single deeply nested group | Empty input, extra spaces, `"(((())))"` |
| Java/8 `sumProduct` | 2/5 | 1/3 | Medium | Singleton; zero in list; negative values | Singleton, zero product, negative product |
| Java/9 `rollingMax` | 2/5 | 1/3 | Medium | Empty list; descending sequence; all-negative sequence | Empty, descending, negative values |
| Java/10 `makePalindrome` | 2/5 | 2/4 | Medium | Already-palindrome input; two-character non-palindrome; palindromic suffix reuse | `"racecar"`, `"ab"`, `"abab"` |
| Java/12 `longest` | 3/5 | 2/4 | Medium | Single empty string; longest value in middle; explicit tie for longer strings | Tie case, empty string value, middle longest |
| Java/13 `greatestCommonDivisor` | 2/5 | 0/3 | Low | `a=0`; `b=0`; `a=b` | `(0,5)`, `(5,0)`, `(9,9)` |
| Java/14 `allPrefixes` | 1/3 | 0/3 | Low | Empty string; single-character string; two-character boundary | `""`, `"x"`, `"ab"` |
| Java/15 `stringSequence` | 2/3 | 1/3 | Medium | Transition from `n=0` to `n=1`; small inclusive sequence | `n=1`, `n=2` |
| Java/17 `parseMusic` | 2/5 | 1/3 | Medium | Empty string; individual note-token classes | Empty, whole note, half plus quarter |
| Java/19 `sortNumbers` | 4/6 | 2/4 | Medium | Full zero-to-nine domain; duplicate number words | All words reversed, duplicates |
| Java/20 `findClosestElements` | 3/5 | 2/3 | Medium | Exactly two elements; negative closest pair | Two-element list, negative pair, duplicate closest pair |
| Java/23 `strlen` | 2/4 | 2/3 | Medium | Whitespace-only string; embedded spaces | Single space, `"hello world"` |
| Java/25 `factorize` | 3/5 | 1/3 | Medium | Lower boundary `n=1`; prime-only input | `1`, `13`, `16` |
| Java/28 `concatenate` | 2/5 | 1/3 | Medium | Singleton; empty-string elements; multi-character element boundaries | Singleton, empty elements, multi-character elements |
| Java/32 `findZero` | 2/4 | 1/3 | Medium | Additional valid polynomial shapes; root requiring different search behavior | Linear `2 - 4x`, cubic `x^3 - 8` |
| Java/33 `sortThird` | 2/5 | 0/3 | Low | Empty list; short list; first sortable pair at indices `0` and `3` | Empty, short list, index `0/3` swap |
| Java/36 `fizzBuzz` | 3/5 | 2/4 | Medium | `n=0`; exclusive upper boundary at `77` | `0`, `77`, `78` |
| Java/38 `decodeCyclic` | 3/5 | 1/4 | Medium | Explicit empty input; length below group size; partial trailing group | Empty, short group, mixed full/partial groups |
| Java/39 `primeFib` | 4/5 | 2/3 | High | Later search depth beyond first five prime Fibonacci values | `n=6` |
| Java/40 `triplesSumToZero` | 3/6 | 1/4 | Medium | Empty list; exactly three zeroes; exactly-three-value true case | Empty, `[0,0,0]`, `[-1,0,1]` |
| Java/44 `changeBase` | 2/5 | 1/4 | Low | `x < base`; exact power of base; largest valid base below 10 | `5` base 8, `16` base 2, `80` base 9 |
| Java/46 `fib4` | 1/3 | 0/4 | Low | All explicit base cases `n=0..3` | `n=0`, `n=1`, `n=2`, `n=3` |
| Java/49 `modp` | 4/5 | 3/4 | High | Modulus boundary where result is zero | `(1,2)`, `(5,7)`, `(10,17)` |
| Java/50 `decodeShift` | 3/5 | 2/4 | High | Explicit empty/single-character and alphabet wraparound boundaries | Empty, `"abcxyz"` round trip, `"z"` round trip |

## Effectiveness Assessment

The base tests are strongest when the original prompt already includes several representative examples, such as `Java/39`, `Java/49`, and `Java/50`. Even there, they still leave some boundary behavior implicit.

The weakest base tests are tasks where the prompt examples only exercise ordinary nominal inputs. Examples include `Java/2`, `Java/4`, `Java/13`, `Java/14`, `Java/33`, `Java/44`, and `Java/46`. These miss core boundary conditions such as integer decimal parts, singleton data sets, zero arguments, empty strings, short lists, base-conversion edge cases, and recurrence base cases.

The generated `BlackBoxTest.java` files make the tests more effective by adding input mutations based on the missing classes. They keep the original base tests by calling `BaseTest.main(args)`, then add focused assertions with failure messages. The mutation suite currently compiles and passes for all 30 tasks, as recorded in `black_box_test_results.json`.
