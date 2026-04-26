# Black-Box Equivalence Class Analysis

Scope: 30 Java HumanEval tasks listed in `prompts.txt`. Base tests are the saved `BaseTest.java` files. Added tests are saved as `BlackBoxTest.java` in each corresponding `generated_java/Java_<id>` directory and call `BaseTest.main(args)` before executing mutated inputs.

## Summary

| Metric | Result |
|---|---:|
| Tasks analyzed | 30 |
| Base test suites assessed | 30 |
| Tasks with meaningful base-test gaps | 30 |
| Black-box mutation suites generated | 30 |
| Black-box mutation suites passing | 30/30 |
| Main gap pattern | Base tests mostly cover nominal examples but miss empty/singleton, exact-boundary, duplicate, tie, zero, negative, and wraparound classes. |

For the explicit effectiveness scoring of the original base tests, see `base_test_effectiveness_assessment.md`.

## Equivalence Classes, Boundaries, And Test Coverage

| Task | Valid Equivalence Classes | Invalid / Out-of-Contract Classes | Boundary Conditions | Base-Test Effectiveness | Added Mutation Tests |
|---|---|---|---|---|---|
| Java/0 `hasCloseElements` | empty/single list; no pair below threshold; pair below threshold; exact duplicate; negative/mixed doubles | null list; negative threshold; NaN/infinite values | size 0/1; distance exactly threshold; distance just below threshold | Covers nominal true/false and duplicate, but misses empty/single, exact threshold, negative values | empty, singleton, exact-threshold false, negative close pair |
| Java/1 `separateParenGroups` | empty string; one group; adjacent groups; spaced groups; nested balanced groups | malformed or unbalanced parentheses; non-parenthesis chars | empty input; no separator between groups; whitespace inside/outside groups | Covers multiple balanced groups and nesting, but misses empty and adjacent groups without spaces | empty string, `()()`, outer-space wrapped group |
| Java/2 `truncateNumber` | integer-valued positive; fraction-only positive; large positive decimal | negative numbers; NaN/infinite values | decimal part 0; number between 0 and 1; precision tolerance | Covers normal decimals, misses integer and small fraction boundary | `7.0`, `0.125`, large decimal |
| Java/3 `belowZero` | empty operation list; never below zero; exactly returns to zero; first operation below zero; later dip below zero | null list; non-integer operations | empty; balance exactly 0; first negative op | Covers nominal safe/unsafe sequences, misses empty and exact-zero boundary | empty list, exact-zero sequence, first withdrawal |
| Java/4 `meanAbsoluteDeviation` | single value; identical values; positive spread; mixed sign spread | empty list; null values; NaN/infinite values | list size 1; all deviations 0; mean at 0 | Covers nominal positive spread, misses singleton, identical, mixed-sign partitions | singleton, identical values, mixed signs |
| Java/5 `intersperse` | empty list; singleton; multiple elements; negative values; arbitrary delimiter | null list | size 0/1/2; delimiter equals negative or existing value | Covers empty and nominal multiple values, misses singleton and negative delimiter/value cases | singleton, negative inputs, negative delimiter |
| Java/6 `parseNestedParens` | empty input; one group; multiple groups; extra spaces; shallow/deep nesting | malformed parentheses; non-parenthesis chars | empty string; consecutive spaces; depth 1 vs deep nesting | Covers normal nesting, misses empty and repeated-space parsing | empty input, extra spaces, deep group |
| Java/8 `sumProduct` | empty list; singleton; positive values; values containing zero; negative values | null list; overflow-prone very large products | empty identity `[0,1]`; singleton; product zero | Covers empty and positive values, misses singleton, zero, negative sign behavior | singleton, zero product, negative product |
| Java/9 `rollingMax` | empty list; ascending; descending; repeated values; negative values | null list | size 0; first value maximum; all values negative | Covers nominal rolling increase, misses empty, descending, negative sequences | empty, descending, negative inputs |
| Java/10 `makePalindrome` | empty string; already palindrome; non-palindrome; palindromic suffix; short two-char input | null string | length 0/1/2; whole string already palindrome | Covers empty and nominal non-palindromes, misses already-palindrome and short boundary | `racecar`, `ab`, `abab` |
| Java/12 `longest` | empty list; singleton; unique longest; tied longest returns first; empty string value | null list; null string element | list size 0/1; tie at max length | Covers empty, ties of length 1, increasing lengths; misses single empty value and middle longest | first tie, single empty string, middle longest |
| Java/13 `greatestCommonDivisor` | coprime; common divisor; equal inputs; one input zero | both inputs zero; negative inputs if not specified | `a=0`; `b=0`; `a=b`; `a>b` vs `a<b` | Covers coprime and common divisor, misses zero and equal boundaries | zero-left, zero-right, equal values |
| Java/14 `allPrefixes` | empty string; single char; multi-char | null string | length 0/1/2 | Covers nominal multi-char only | empty, single-char, two-char |
| Java/15 `stringSequence` | `n=0`; small positive `n`; larger positive `n` | negative `n` | inclusive lower boundary 0; transition from 0 to 1 | Covers `0` and larger value, misses small transition `1` | `n=1`, `n=2` |
| Java/17 `parseMusic` | empty string; whole note; half note; quarter note; mixed sequence | unknown tokens; malformed spacing | empty input; single token; all token kinds | Covers mixed notes, misses empty and individual-token boundaries | empty, whole note, half+quarter |
| Java/19 `sortNumbers` | empty; singleton; already sorted; reverse order; all digit words; duplicates | unknown word; malformed capitalization | empty string; all ten values; duplicates | Covers empty, singleton, already sorted subset, partial unsorted; misses full domain and duplicates | all words reversed, duplicate words |
| Java/20 `findClosestElements` | exactly two elements; duplicate closest; negative values; general list | list length < 2; NaN/infinite values | length 2; distance 0; negative closest pair | Covers nominal and duplicate pair, misses exact length and negative values | two-element list, negative pair, duplicate pair |
| Java/23 `strlen` | empty string; non-empty letters; spaces included | null string | length 0; length 1; embedded spaces | Covers empty and alphabetic string, misses spaces | single space, embedded space |
| Java/25 `factorize` | `1`; prime; repeated prime powers; composite with distinct factors | n <= 0 | lower boundary 1; prime vs composite; repeated factor | Covers repeated and mixed composites, misses `1` and prime-only inputs | `1`, prime 13, power 16 |
| Java/28 `concatenate` | empty list; singleton; multiple strings; empty-string elements | null list; null element | list size 0/1; empty element | Covers empty and nominal multiple, misses singleton and empty element behavior | singleton, empty elements, multi-char elements |
| Java/32 `findZero` | linear polynomial; higher-degree polynomial; root requiring search expansion | odd number of coefficients; highest coefficient zero; no guaranteed real root | 2 coefficients; expanded interval; root near endpoint | Covers linear and one cubic, misses additional valid polynomial shape | valid linear root, valid cubic `x^3-8` root |
| Java/33 `sortThird` | empty list; fewer than 3 elements; exactly indices 0 and 3; normal longer list; duplicates | null list | length 0/1/2; first sortable pair at index 3 | Covers nominal longer lists, misses empty/short and exact sortable pair | empty, short list, index 0/3 swap |
| Java/36 `fizzBuzz` | n below first candidate; n excluding 77; n including 77; larger ranges | negative n | exclusive upper boundary; `n=77` vs `n=78`; `n=0` | Covers around 78/79 and normal low value, misses 0 and exclusive 77 boundary | `0`, `77`, `78` |
| Java/38 `decodeCyclic` | empty; length < 3; exactly/multiple groups of 3; mixed full/partial groups | null string | length 0/1/2/3; trailing partial group | Covers randomized round trips, misses explicit length boundaries | empty, short group, mixed groups |
| Java/39 `primeFib` | first prime Fibonacci; early sequence; later sequence | n <= 0 | `n=1`; transition to later search | Covers first five values, still misses later search depth | `n=1`, `n=6` |
| Java/40 `triplesSumToZero` | fewer than 3 values; exactly 3 true; exactly 3 false; duplicate zeroes; larger true/false lists | null list | size 0/1/2/3; distinct indices with equal values | Covers nominal true/false and size 1, misses empty and all-zero triple | empty, `[0,0,0]`, `[-1,0,1]` |
| Java/44 `changeBase` | x smaller than base; exact power of base; normal conversion; base 2; base 9 | base < 2; base >= 10; negative x | x < base; exact powers; largest allowed base below 10 | Covers normal base 2/3 conversion, misses x<base, exact power, base 9 | `5` base 8, `16` base 2, `80` base 9 |
| Java/46 `fib4` | base cases n=0..3; recurrence values n>=4 | negative n | transition from base cases to recurrence | Covers recurrence values only, misses all explicit base cases | n=0,1,2,3 |
| Java/49 `modp` | n=0; p divides 2^n; non-trivial remainder; larger exponent | p <= 1; negative n | exponent 0/1; modulus 2; repeated modular reduction | Covers n=0 and several normal values, misses modulus-2/divisible case | `(1,2)`, `(5,7)`, `(10,17)` |
| Java/50 `decodeShift` | empty; single char; wraparound letters; longer string | null string; non-lowercase chars if unsupported | length 0/1; `z` wraparound | Covers randomized round trips, misses explicit empty/single/wrap boundaries | empty, `abcxyz` round trip, `z` round trip |

## Generated Test Files

Each task now has `generated_java/Java_<id>/BlackBoxTest.java`. These tests are input mutations around the base tests and focus on equivalence classes and boundary values. They are not a replacement for invalid-input robustness tests because most HumanEval prompts do not define behavior for invalid inputs.

## Execution Result

`black_box_test_results.json` records the execution result. Current result: 30/30 `BlackBoxTest.java` suites compile and pass.
