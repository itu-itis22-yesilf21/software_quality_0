/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

import java.util.*;
import java.lang.*;

public class ImprovedBaseTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.parseNestedParens("").equals(Arrays.asList()), "empty input should produce no nesting depths");
        check(s.parseNestedParens("()  (())").equals(Arrays.asList(1, 2)), "extra spaces should be ignored between parenthesis groups");    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
