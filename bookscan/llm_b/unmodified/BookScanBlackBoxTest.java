/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Phase 2 / Step 9 mutation black-box suite for
 * bookscan/llm_b/unmodified/BookScan.java (Gemini 3.1 Pro unmodified+combined).
 *
 * Adds focused tests for the equivalence classes that
 * BookScanIntegrationTest.java (Step 5 Run 5.2) did not assert against,
 * drawn from the mutation taxonomy in bookscan/reports/ecp_bva_table.md.
 *
 * Same SPEC-DIVERGENCE convention as the LLM A unmodified suite: where
 * Gemini's implementation contradicts the brief (MixedCase miss in the
 * case-folding integration; substring-of-longer-word false positives
 * in the per-line howManyTimes), this suite asserts the *actual*
 * behaviour with a comment naming the divergence. The companion
 * BookScanIntegrationTest already records the spec-aligned failures
 * (Step 6), so this suite stays green and provides positive coverage
 * for the under-tested boundary classes.
 */
public class BookScanBlackBoxTest {

    private final BookScan scanner = new BookScan();

    // ---- V2 / B5: Single-line, single matching word ----
    @Test
    void v2_singleLineSingleWord() {
        Map<String, BookScan.WordStats> r = scanner.scanWordsOfLength("cat", 3);
        assertNotNull(r.get("cat"),
            "V2: minimal single-line text should produce key 'cat'");
        assertEquals(1, r.get("cat").getTotalOccurrences(),
            "V2: count is 1 for single occurrence");
    }

    // ---- V5: Same word on non-consecutive lines ----
    @Test
    void v5_nonConsecutiveLines() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("cat\ndog\nfox\nfox\ncat", 3);
        // Per-line howManyTimes counts each line independently; lines
        // are deduplicated. cat appears on line 1 and line 5.
        assertEquals(2, r.get("cat").getTotalOccurrences(),
            "V5: 'cat' appears twice across non-consecutive lines");
        assertEquals(Arrays.asList(1, 5), r.get("cat").getLines(),
            "V5: lines list contains 1 and 5");
    }

    // ---- V8: Mixed punctuation as separators ----
    // Tokeniser is split("[^a-zA-Z]+"), so every non-letter splits.
    @Test
    void v8_mixedPunctuation() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("cat,dog;rat!fox?bug", 3);
        assertTrue(r.containsKey("cat") && r.containsKey("dog")
                && r.containsKey("rat") && r.containsKey("fox")
                && r.containsKey("bug"),
            "V8: each punctuation char splits the next token");
    }

    // ---- V9: Tabs and multiple spaces ----
    @Test
    void v9_tabsAndMultiSpaces() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("cat\t\tdog    fox", 3);
        assertEquals(3, r.size(),
            "V9: tabs / multispaces tokenise to 3 distinct length-3 words");
    }

    // ---- V11: Text containing only the target word repeated ----
    @Test
    void v11_onlyTargetWordRepeated() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("the the the the", 3);
        // SPEC-DIVERGENCE: spec expects lines=[1,1,1,1] per-occurrence;
        // this variant uses howManyTimes(line, "the") + howManyTimes(line, "THE")
        // and dedups lines, so getTotalOccurrences == 4 but getLines == [1].
        assertEquals(4, r.get("the").getTotalOccurrences(),
            "V11: count == 4 occurrences of 'the' on line 1");
        assertEquals(Collections.singletonList(1), r.get("the").getLines(),
            "V11: lines list is deduplicated to [1] (variant-specific)");
    }

    // ---- V12: Digits and letters mixed ----
    // The regex is [^a-zA-Z]+ — digits split tokens.
    // SPEC: agrees here (digit-only is not a [A-Za-z] word).
    @Test
    void v12_digitsSplitTokens() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("abc 123 def", 3);
        assertTrue(r.containsKey("abc") && r.containsKey("def"),
            "V12: digit-only token '123' is NOT a word (spec-compliant here)");
        assertFalse(r.containsKey("123"),
            "V12: '123' must not appear as a key");
    }

    // ---- B2: wordLength = 1 with mixed-length tokens ----
    @Test
    void b2_lengthOne() {
        // Note: testStrlenEmptyOneCharacterWords (Run 5.2 Req 6) already
        // exposed the substring-of-longer-word bug on "I have a pen".
        // Here we use a text with NO embeddable length-1 tokens to
        // isolate the length-1 path without triggering the substring
        // bug.
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("X Y Z", 1);
        assertEquals(3, r.size(),
            "B2: three one-char tokens X / Y / Z (lowercase keys) -> 3 entries");
        assertTrue(r.containsKey("x") && r.containsKey("y") && r.containsKey("z"),
            "B2: keys lowercased via .toLowerCase()");
    }

    // ---- B4: wordLength = length of longest token ----
    @Test
    void b4_longestWord() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("hi rocket sun", 6);
        assertEquals(1, r.size(),
            "B4: target length 6 isolates the longest word");
        assertTrue(r.containsKey("rocket"),
            "B4: 'rocket' (length 6) present");
    }

    // ---- B6: Empty line in the middle ----
    @Test
    void b6_emptyMiddleLine() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("cat\n\nfox", 3);
        assertEquals(Collections.singletonList(1), r.get("cat").getLines(),
            "B6: 'cat' stays on line 1");
        assertEquals(Collections.singletonList(3), r.get("fox").getLines(),
            "B6: 'fox' is on line 3 after the empty line 2");
    }

    // ---- B7: Leading / trailing blank lines ----
    @Test
    void b7_leadingTrailingBlankLines() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("\ncat\n", 3);
        assertEquals(Collections.singletonList(2), r.get("cat").getLines(),
            "B7: leading blank line shifts 'cat' to line 2");
    }

    // ---- B8: Whitespace-only line ----
    @Test
    void b8_whitespaceOnlyLine() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("cat\n   \nfox", 3);
        assertEquals(Collections.singletonList(1), r.get("cat").getLines(),
            "B8: 'cat' on line 1");
        assertEquals(Collections.singletonList(3), r.get("fox").getLines(),
            "B8: 'fox' on line 3, whitespace-only line contributes nothing");
    }

    // ---- B9: Apostrophe and hyphen split words ----
    @Test
    void b9_apostropheAndHyphen() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("don't x-ray", 3);
        assertTrue(r.containsKey("don") && r.containsKey("ray"),
            "B9: apostrophe and hyphen split the word — 'don' and 'ray' present");
    }

    // ---- B10: Word repeated on first and last lines only ----
    @Test
    void b10_firstAndLastLines() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("the\ndog\ncat\nfox\nthe", 3);
        assertEquals(2, r.get("the").getTotalOccurrences(),
            "B10: 'the' appears on lines 1 and 5 — count 2");
        assertEquals(Arrays.asList(1, 5), r.get("the").getLines(),
            "B10: lines list contains 1 and 5");
    }

    // ---- I1: Negative wordLength ----
    // Variant has no explicit guard; length -1 never matches any
    // strlen(rawWord), so the result is empty.
    @Test
    void i1_negativeWordLength() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("anything here", -1);
        assertTrue(r.isEmpty(),
            "I1: negative wordLength yields empty result");
    }

    // ---- I4: Non-ASCII letters ----
    // Regex is [^a-zA-Z]+, so non-ASCII letters split tokens.
    // SPEC-DIVERGENCE: brief is silent on Unicode, but the tokenisation
    // here is *more* spec-conforming than the LLM A unmodified variant.
    @Test
    void i4_nonAsciiLetters() {
        Map<String, BookScan.WordStats> r =
            scanner.scanWordsOfLength("Yeşil tree", 3);
        // "Yeşil" splits at "ş" → "Ye" (len 2) and "il" (len 2);
        // neither matches length 3. "tree" is length 4. No length-3
        // tokens, so the result is empty.
        assertTrue(r.isEmpty(),
            "I4: 'Yeşil tree' splits non-ASCII letters; no length-3 tokens remain");
    }
}
