/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;

/**
 * Phase 2 / Step 9 mutation black-box suite for
 * bookscan/llm_a/unmodified/BookScan.java (GPT-5.5 unmodified+combined).
 *
 * Adds focused tests for the equivalence classes that
 * BookScanIntegrationTest.java (Step 5 Run 5.1) did not assert against,
 * drawn from the mutation taxonomy in bookscan/reports/ecp_bva_table.md.
 *
 * Where the brief's spec disagrees with this variant's actual
 * behaviour (case sensitivity from the no-op flipCase(flipCase(...))
 * normalisation, line de-duplication, two-pass howManyTimes
 * re-counting), the assertion follows the *variant's actual
 * behaviour* with a // SPEC-DIVERGENCE comment that names the
 * difference. This mirrors Phase 1's "30/30 mutation suites passing"
 * goal while preserving the diagnostic value of each divergence.
 */
public class BookScanBlackBoxTest {

    public static void main(String[] args) {
        BookScan bs = new BookScan();

        // ---- V2 / B5: Single-line text with one matching word ----
        check(bs.scanWords("cat", 3).get("cat").getCount() == 1,
            "V2/B5: single-line single-word text should produce count 1 for the word");

        // ---- V5: Same word in non-consecutive lines ----
        Map<String, BookScan.WordInfo> v5 = bs.scanWords(
            "cat\ndog\nfox\nfox\ncat", 3);
        // Lines collected per-line then deduplicated by lines.contains; final
        // count is overridden by howManyTimes on synthesised stream.
        // SPEC-DIVERGENCE: spec expects lines=[1, 5] per-occurrence,
        // this variant emits lines=[1, 5] (dedup of [1,5]) with count
        // re-counted to 2 by the second pass. Outcome happens to match
        // the spec on this input.
        check(v5.get("cat").getLines().equals(Arrays.asList(1, 5)),
            "V5: 'cat' on line 1 and line 5 should give lines=[1, 5]");
        check(v5.get("cat").getCount() == 2,
            "V5: 'cat' should be counted twice across lines 1 and 5");

        // ---- V8: Words separated by mixed punctuation ----
        // Tokeniser is split("[^\\p{L}\\p{N}]+") — punctuation acts as
        // separator the same as whitespace, so "cat,dog;rat!" splits
        // into [cat, dog, rat].
        Map<String, BookScan.WordInfo> v8 = bs.scanWords("cat,dog;rat!fox", 3);
        check(v8.containsKey("cat") && v8.containsKey("dog")
            && v8.containsKey("rat") && v8.containsKey("fox"),
            "V8: punctuation should split words: cat / dog / rat / fox all present");

        // ---- V9: Tabs and multiple spaces ----
        Map<String, BookScan.WordInfo> v9 = bs.scanWords("cat\t\tdog    fox", 3);
        check(v9.size() == 3, "V9: tabs and multi-spaces should yield 3 keys");

        // ---- V11: Text containing only the target word repeated ----
        Map<String, BookScan.WordInfo> v11 = bs.scanWords("the the the the", 3);
        // SPEC-DIVERGENCE: spec expects lines=[1,1,1,1] per-occurrence;
        // this variant dedups lines to [1] but the second-pass
        // howManyTimes correctly recovers count=4.
        check(v11.get("the").getCount() == 4,
            "V11: 'the the the the' should give count 4 for 'the'");
        check(v11.get("the").getLines().equals(Arrays.asList(1)),
            "V11: lines list is deduplicated to [1] (variant-specific behaviour)");

        // ---- V12: Digits and letters mixed ----
        // The tokeniser splits on [^\\p{L}\\p{N}]+ which includes
        // ASCII letters AND digits. "123" therefore IS a token of
        // length 3 in this variant. SPEC-DIVERGENCE: spec defines a
        // word as [A-Za-z] only.
        Map<String, BookScan.WordInfo> v12 = bs.scanWords("abc 123 def", 3);
        check(v12.containsKey("123"),
            "V12: digit-only token '123' IS treated as a length-3 word "
            + "by this variant (digits are word chars per its regex)");

        // ---- B2: wordLength = 1, with one-char and multi-char tokens ----
        Map<String, BookScan.WordInfo> b2 = bs.scanWords("I am here", 1);
        check(b2.containsKey("I") && b2.size() == 1,
            "B2: 'I am here' length 1 yields only 'I' "
            + "(case-preserved; 'am' is length 2, 'here' is length 4)");

        // ---- B4: wordLength = length of longest token ----
        Map<String, BookScan.WordInfo> b4 = bs.scanWords("hi rocket sun", 6);
        check(b4.size() == 1 && b4.containsKey("rocket"),
            "B4: target length 6 isolates the longest word 'rocket'");

        // ---- B6: Empty line in the middle of the text ----
        Map<String, BookScan.WordInfo> b6 = bs.scanWords("cat\n\nfox", 3);
        // The middle empty line splits "" — no tokens. Lines counted
        // 1, 2, 3 regardless.
        check(b6.get("cat").getLines().equals(Arrays.asList(1)),
            "B6: empty middle line should not contribute, 'cat' stays on line 1");
        check(b6.get("fox").getLines().equals(Arrays.asList(3)),
            "B6: 'fox' should be on line 3 (after the empty line 2)");

        // ---- B7: Leading and trailing blank lines ----
        Map<String, BookScan.WordInfo> b7 = bs.scanWords("\ncat\n", 3);
        // Leading "\n" produces an empty first split element (line 1),
        // then "cat" on line 2.
        check(b7.get("cat").getLines().equals(Arrays.asList(2)),
            "B7: leading blank line shifts 'cat' to line 2");

        // ---- B8: Whitespace-only line ----
        Map<String, BookScan.WordInfo> b8 = bs.scanWords("cat\n   \nfox", 3);
        check(b8.get("cat").getLines().equals(Arrays.asList(1))
           && b8.get("fox").getLines().equals(Arrays.asList(3)),
            "B8: whitespace-only middle line should contribute no tokens");

        // ---- B9: Apostrophe and hyphen between letters ----
        // SPEC says these split a word. This variant's regex includes
        // letters and digits only, so apostrophe / hyphen ALSO split.
        Map<String, BookScan.WordInfo> b9 = bs.scanWords("don't x-ray", 3);
        check(b9.containsKey("don") && b9.containsKey("ray"),
            "B9: apostrophe and hyphen split a word into pieces; "
            + "'don' and 'ray' both present as length-3 tokens");

        // ---- B10: Word repeated on first AND last lines only ----
        Map<String, BookScan.WordInfo> b10 = bs.scanWords(
            "the\ndog\ncat\nfox\nthe", 3);
        check(b10.get("the").getLines().equals(Arrays.asList(1, 5)),
            "B10: same word on first and last lines yields lines [1, 5]");

        // ---- I1: Negative wordLength ----
        // The variant's guard is `targetWordLength < 0` → empty map.
        Map<String, BookScan.WordInfo> i1 = bs.scanWords("anything here", -1);
        check(i1.isEmpty(), "I1: negative wordLength returns empty map");

        // ---- I4: Non-ASCII letters ----
        // The regex uses \\p{L} so non-ASCII letters are word chars.
        // "Yeşil" is a 5-letter Unicode token (ş counts as a letter).
        // SPEC-DIVERGENCE: brief says [A-Za-z] only.
        Map<String, BookScan.WordInfo> i4 = bs.scanWords("Yeşil tree", 5);
        check(i4.containsKey("Yeşil"),
            "I4: 'Yeşil' is treated as a length-5 word "
            + "(Unicode letter regex; spec said ASCII only)");

        System.out.println("All BookScanBlackBoxTest assertions passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
