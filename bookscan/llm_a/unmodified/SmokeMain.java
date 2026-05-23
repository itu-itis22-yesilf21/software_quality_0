/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;

/**
 * Phase 2 / Step 4 smoke test for bookscan/llm_a/unmodified/BookScan.java.
 *
 * This driver does NOT validate the integration semantics (the unmodified
 * prompt left those semantics undefined). It only confirms:
 *   1. BookScan compiles and instantiates.
 *   2. The three required helpers (howManyTimes, strlen, flipCase) return
 *      the values shown in their HumanEval Javadoc examples.
 *   3. The integration method (scanWords here) can be called on a small
 *      5-line text without throwing.
 *
 * The integration output is printed for the Step 5 reviewer; it is not
 * asserted because the unmodified variant invented its own output type.
 */
public class SmokeMain {
    public static void main(String[] args) {
        BookScan bs = new BookScan();

        // 1. HumanEval helper checks.
        check(bs.howManyTimes("", "a") == 0, "howManyTimes(\"\", \"a\") == 0");
        check(bs.howManyTimes("aaa", "a") == 3, "howManyTimes(\"aaa\", \"a\") == 3");
        check(bs.howManyTimes("aaaa", "aa") == 3, "howManyTimes(\"aaaa\", \"aa\") == 3");
        check(bs.strlen("") == 0, "strlen(\"\") == 0");
        check(bs.strlen("abc") == 3, "strlen(\"abc\") == 3");
        check(bs.flipCase("Hello").equals("hELLO"), "flipCase(\"Hello\") == \"hELLO\"");

        // 2. Integration call (signature-specific to this variant).
        String text = String.join("\n",
            "The cat sat on the mat",
            "On the MAT was a rat",
            "the dog ran",
            "A cat chased the mouse",
            "the the the");
        Map<String, BookScan.WordInfo> result = bs.scanWords(text, 3);
        System.out.println("scanWords(text, 3) -> " + result);

        System.out.println("SMOKE OK");
    }

    private static void check(boolean cond, String msg) {
        if (!cond) {
            System.err.println("SMOKE FAIL: " + msg);
            System.exit(1);
        }
    }
}
