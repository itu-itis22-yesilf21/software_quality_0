/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Phase 2 / Step 9 mutation black-box suite for
 * bookscan/llm_b/edited/BookScan.java (Gemini 3.1 Pro edited+combined).
 *
 * Same conceptual mutation set as the LLM A edited suite, rewritten
 * in JUnit 5 to match this variant's existing integration suite
 * idiom. Like the LLM A edited variant, this implementation matches
 * the Variant 2 spec character-for-character (Step 3 finding: 13/13
 * spec items met), so every assertion below asserts the brief's
 * expected behaviour and the suite is green by construction.
 */
public class BookScanBlackBoxTest {

    private final BookScan scanner = new BookScan();

    // ---- V2 / B5: Single-line, single matching word ----
    @Test
    void v2_singleLineSingleWord() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("cat"), 3);
        assertEquals(Collections.singletonList(1), r.get("cat"),
            "V2/B5: single-line single-word text yields {cat:[1]}");
    }

    // ---- V5: Same word on non-consecutive lines ----
    @Test
    void v5_nonConsecutiveLines() {
        Map<String, List<Integer>> r = scanner.scan(
            Arrays.asList("cat", "dog", "fox", "fox", "cat"), 3);
        assertEquals(Arrays.asList(1, 5), r.get("cat"),
            "V5: 'cat' on lines 1 and 5 produces [1, 5] per occurrence");
    }

    // ---- V8: Mixed punctuation as separators ----
    @Test
    void v8_mixedPunctuation() {
        Map<String, List<Integer>> r = scanner.scan(
            Collections.singletonList("cat,dog;rat!fox?bug"), 3);
        assertTrue(r.containsKey("cat") && r.containsKey("dog")
                && r.containsKey("rat") && r.containsKey("fox")
                && r.containsKey("bug"),
            "V8: punctuation splits tokens; five length-3 words present");
    }

    // ---- V9: Tabs and multiple spaces ----
    @Test
    void v9_tabsAndMultiSpaces() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("cat\t\tdog    fox"), 3);
        assertEquals(3, r.size(),
            "V9: tabs and multispaces split into 3 length-3 words");
    }

    // ---- V11: Text containing only the target word repeated ----
    @Test
    void v11_onlyTargetWordRepeated() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("the the the the"), 3);
        assertEquals(Arrays.asList(1, 1, 1, 1), r.get("the"),
            "V11: per-occurrence repetition yields [1,1,1,1] for "
            + "'the the the the' on line 1");
    }

    // ---- V12: Digits split tokens (spec: [A-Za-z] only) ----
    @Test
    void v12_digitsSplitTokens() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("abc 123 def"), 3);
        assertTrue(r.containsKey("abc") && r.containsKey("def"),
            "V12: 'abc' and 'def' present at length 3");
        assertFalse(r.containsKey("123"),
            "V12: digit-only token '123' is not a word");
    }

    // ---- B2: wordLength = 1 with one-char words ----
    @Test
    void b2_lengthOne() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("X Y Z"), 1);
        assertEquals(3, r.size(),
            "B2: three one-char tokens X / Y / Z");
        assertTrue(r.containsKey("x") && r.containsKey("y") && r.containsKey("z"),
            "B2: keys lowercased via normalizeToLowercase -> flipCase");
    }

    // ---- B4: wordLength = length of longest token ----
    @Test
    void b4_longestWord() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("hi rocket sun"), 6);
        assertEquals(1, r.size(),
            "B4: length 6 isolates 'rocket'");
        assertTrue(r.containsKey("rocket"),
            "B4: 'rocket' (length 6) is the only key");
    }

    // ---- B6: Empty line in the middle ----
    @Test
    void b6_emptyMiddleLine() {
        Map<String, List<Integer>> r =
            scanner.scan(Arrays.asList("cat", "", "fox"), 3);
        assertEquals(Collections.singletonList(1), r.get("cat"),
            "B6: 'cat' stays on line 1");
        assertEquals(Collections.singletonList(3), r.get("fox"),
            "B6: 'fox' is on line 3 after the empty middle line");
    }

    // ---- B7: Leading and trailing blank lines ----
    @Test
    void b7_leadingTrailingBlankLines() {
        Map<String, List<Integer>> r =
            scanner.scan(Arrays.asList("", "cat", ""), 3);
        assertEquals(Collections.singletonList(2), r.get("cat"),
            "B7: leading blank line shifts 'cat' to line 2");
    }

    // ---- B8: Whitespace-only line ----
    @Test
    void b8_whitespaceOnlyLine() {
        Map<String, List<Integer>> r =
            scanner.scan(Arrays.asList("cat", "   ", "fox"), 3);
        assertEquals(Collections.singletonList(1), r.get("cat"),
            "B8: 'cat' on line 1");
        assertEquals(Collections.singletonList(3), r.get("fox"),
            "B8: whitespace-only line contributes no tokens");
    }

    // ---- B9: Apostrophe and hyphen split words ----
    @Test
    void b9_apostropheAndHyphen() {
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("don't x-ray"), 3);
        assertTrue(r.containsKey("don") && r.containsKey("ray"),
            "B9: apostrophe and hyphen split tokens (spec: not word chars)");
    }

    // ---- B10: Word repeated on first and last lines only ----
    @Test
    void b10_firstAndLastLines() {
        Map<String, List<Integer>> r = scanner.scan(
            Arrays.asList("the", "dog", "cat", "fox", "the"), 3);
        assertEquals(Arrays.asList(1, 5), r.get("the"),
            "B10: 'the' on first and last lines yields [1, 5]");
    }

    // ---- I1: Negative wordLength ----
    @Test
    void i1_negativeWordLength() {
        assertTrue(scanner.scan(Collections.singletonList("anything"), -1).isEmpty(),
            "I1: negative wordLength returns empty map");
        assertTrue(scanner.scan(Collections.singletonList("anything"), -100).isEmpty(),
            "I1: very negative wordLength returns empty map");
    }

    // ---- I4: Non-ASCII letters ----
    @Test
    void i4_nonAsciiLetters() {
        // The regex is split("[^A-Za-z]+"), so non-ASCII letters split.
        // "Yeşil" -> "Ye" / "il", "tree" -> "tree". No length-3 tokens.
        Map<String, List<Integer>> r =
            scanner.scan(Collections.singletonList("Yeşil tree"), 3);
        assertTrue(r.isEmpty(),
            "I4: 'Yeşil tree' splits at 'ş'; no length-3 tokens remain");
    }
}
