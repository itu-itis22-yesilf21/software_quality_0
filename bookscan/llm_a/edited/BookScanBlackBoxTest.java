/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;

/**
 * Phase 2 / Step 9 mutation black-box suite for
 * bookscan/llm_a/edited/BookScan.java (GPT-5.5 edited+combined).
 *
 * Adds focused tests for the equivalence classes
 * BookScanIntegrationTest.java (Step 5 Run 5.3) did not assert
 * against, drawn from the mutation taxonomy in
 * bookscan/reports/ecp_bva_table.md.
 *
 * This variant matches the Variant 2 spec character-for-character
 * (Step 3 finding: 13/13 spec items met), so every assertion below is
 * the brief's expected behaviour. The suite is therefore green by
 * construction and serves as a positive boundary-coverage suite.
 */
public class BookScanBlackBoxTest {

    public static void main(String[] args) {
        BookScan bs = new BookScan();

        // ---- V2 / B5: Single-line, single matching word ----
        Map<String, List<Integer>> v2 =
            bs.scan(Collections.singletonList("cat"), 3);
        check(v2.get("cat").equals(Collections.singletonList(1)),
            "V2/B5: single-line single-word text yields {cat:[1]}");

        // ---- V5: Same word on non-consecutive lines ----
        Map<String, List<Integer>> v5 = bs.scan(
            Arrays.asList("cat", "dog", "fox", "fox", "cat"), 3);
        check(v5.get("cat").equals(Arrays.asList(1, 5)),
            "V5: 'cat' on lines 1 and 5 produces [1, 5] per occurrence");

        // ---- V8: Mixed punctuation as separators ----
        Map<String, List<Integer>> v8 =
            bs.scan(Collections.singletonList("cat,dog;rat!fox?bug"), 3);
        check(v8.containsKey("cat") && v8.containsKey("dog")
           && v8.containsKey("rat") && v8.containsKey("fox")
           && v8.containsKey("bug"),
            "V8: punctuation splits tokens; five length-3 words present");

        // ---- V9: Tabs and multiple spaces ----
        Map<String, List<Integer>> v9 =
            bs.scan(Collections.singletonList("cat\t\tdog    fox"), 3);
        check(v9.size() == 3, "V9: tabs and multispaces split into 3 words");

        // ---- V11: Text containing only the target word repeated ----
        Map<String, List<Integer>> v11 =
            bs.scan(Collections.singletonList("the the the the"), 3);
        check(v11.get("the").equals(Arrays.asList(1, 1, 1, 1)),
            "V11: per-occurrence repetition yields [1,1,1,1] for "
            + "'the the the the' on line 1");

        // ---- V12: Digits split tokens (spec: [A-Za-z] only) ----
        Map<String, List<Integer>> v12 =
            bs.scan(Collections.singletonList("abc 123 def"), 3);
        check(v12.containsKey("abc") && v12.containsKey("def")
           && !v12.containsKey("123"),
            "V12: digits split tokens; '123' is not a key");

        // ---- B2: wordLength = 1 with one-char words ----
        Map<String, List<Integer>> b2 =
            bs.scan(Collections.singletonList("X Y Z"), 1);
        check(b2.size() == 3
           && b2.containsKey("x") && b2.containsKey("y") && b2.containsKey("z"),
            "B2: three single-char tokens lower-cased to x / y / z");

        // ---- B4: wordLength = length of longest token ----
        Map<String, List<Integer>> b4 =
            bs.scan(Collections.singletonList("hi rocket sun"), 6);
        check(b4.size() == 1 && b4.containsKey("rocket"),
            "B4: target length 6 isolates 'rocket'");

        // ---- B6: Empty line in the middle ----
        Map<String, List<Integer>> b6 =
            bs.scan(Arrays.asList("cat", "", "fox"), 3);
        check(b6.get("cat").equals(Collections.singletonList(1))
           && b6.get("fox").equals(Collections.singletonList(3)),
            "B6: empty middle line contributes nothing; line numbers preserved");

        // ---- B7: Leading and trailing blank lines ----
        Map<String, List<Integer>> b7 =
            bs.scan(Arrays.asList("", "cat", ""), 3);
        check(b7.get("cat").equals(Collections.singletonList(2)),
            "B7: leading blank line shifts 'cat' to line 2");

        // ---- B8: Whitespace-only line ----
        Map<String, List<Integer>> b8 =
            bs.scan(Arrays.asList("cat", "   ", "fox"), 3);
        check(b8.get("cat").equals(Collections.singletonList(1))
           && b8.get("fox").equals(Collections.singletonList(3)),
            "B8: whitespace-only line contributes no tokens");

        // ---- B9: Apostrophe and hyphen split words ----
        Map<String, List<Integer>> b9 =
            bs.scan(Collections.singletonList("don't x-ray"), 3);
        check(b9.containsKey("don") && b9.containsKey("ray"),
            "B9: apostrophe and hyphen split tokens (spec: not word chars)");

        // ---- B10: Word repeated on first and last lines only ----
        Map<String, List<Integer>> b10 = bs.scan(
            Arrays.asList("the", "dog", "cat", "fox", "the"), 3);
        check(b10.get("the").equals(Arrays.asList(1, 5)),
            "B10: 'the' on first and last lines yields [1, 5]");

        // ---- I1: Negative wordLength ----
        check(bs.scan(Collections.singletonList("anything"), -1).isEmpty(),
            "I1: negative wordLength returns empty map");
        check(bs.scan(Collections.singletonList("anything"), -100).isEmpty(),
            "I1: very negative wordLength returns empty map");

        // ---- I4: Non-ASCII letters ----
        // The variant's isAsciiLetter helper returns false for non-ASCII,
        // so "Yeşil" splits at 'ş' into "Ye" / "il". Neither is length 3.
        Map<String, List<Integer>> i4 =
            bs.scan(Collections.singletonList("Yeşil tree"), 3);
        check(i4.isEmpty(),
            "I4: 'Yeşil tree' splits at non-ASCII letters; no length-3 tokens");

        System.out.println("All BookScanBlackBoxTest assertions passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
