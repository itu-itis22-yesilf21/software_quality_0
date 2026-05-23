/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;

/**
 * Phase 2 / Step 4 smoke test for bookscan/llm_b/edited/BookScan.java.
 * Identical assertions to the LLM A edited driver, because the edited
 * prompt pinned the same scan(List<String>, int) -> Map<String, List<Integer>>
 * contract for both LLMs.
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

        List<String> lines = Arrays.asList(
            "The cat sat on the mat",
            "On the MAT was a rat",
            "the dog ran",
            "A cat chased the mouse",
            "the the the");
        Map<String, List<Integer>> result = bs.scan(lines, 3);
        System.out.println("scan(lines, 3) -> " + result);

        check(result.containsKey("the"), "result contains key 'the'");
        check(result.get("the").equals(Arrays.asList(1, 1, 2, 3, 4, 5, 5, 5)),
            "result.get('the') == [1, 1, 2, 3, 4, 5, 5, 5]");
        check(result.get("cat").equals(Arrays.asList(1, 4)),
            "result.get('cat') == [1, 4]");
        check(result.get("mat").equals(Arrays.asList(1, 2)),
            "result.get('mat') == [1, 2]");

        check(bs.scan(null, 3).isEmpty(), "scan(null, 3) returns empty map");
        check(bs.scan(lines, 0).isEmpty(), "scan(lines, 0) returns empty map");
        check(bs.scan(lines, -1).isEmpty(), "scan(lines, -1) returns empty map");
        check(bs.scan(Arrays.asList((String) null), 3).isEmpty(),
            "scan([null], 3) treats null line as empty -> empty map");

        System.out.println("SMOKE OK");
    }

    private static void check(boolean cond, String msg) {
        if (!cond) {
            System.err.println("SMOKE FAIL: " + msg);
            System.exit(1);
        }
    }
}
