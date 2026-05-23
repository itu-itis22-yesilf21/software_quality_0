/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;

/**
 * Phase 2 / Step 4 smoke test for bookscan/llm_b/unmodified/BookScan.java.
 * Mirrors the LLM A unmodified smoke driver but calls
 * scanWordsOfLength(String, int) -> Map<String, BookScan.WordStats>,
 * the API name this variant invented.
 */
public class SmokeMain {
    public static void main(String[] args) {
        BookScan bs = new BookScan();

        check(bs.howManyTimes("", "a") == 0, "howManyTimes(\"\", \"a\") == 0");
        check(bs.howManyTimes("aaa", "a") == 3, "howManyTimes(\"aaa\", \"a\") == 3");
        check(bs.howManyTimes("aaaa", "aa") == 3, "howManyTimes(\"aaaa\", \"aa\") == 3");
        check(bs.strlen("") == 0, "strlen(\"\") == 0");
        check(bs.strlen("abc") == 3, "strlen(\"abc\") == 3");
        check(bs.flipCase("Hello").equals("hELLO"), "flipCase(\"Hello\") == \"hELLO\"");

        String text = String.join("\n",
            "The cat sat on the mat",
            "On the MAT was a rat",
            "the dog ran",
            "A cat chased the mouse",
            "the the the");
        Map<String, BookScan.WordStats> result = bs.scanWordsOfLength(text, 3);
        System.out.println("scanWordsOfLength(text, 3) -> " + result);

        System.out.println("SMOKE OK");
    }

    private static void check(boolean cond, String msg) {
        if (!cond) {
            System.err.println("SMOKE FAIL: " + msg);
            System.exit(1);
        }
    }
}
