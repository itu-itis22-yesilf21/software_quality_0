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
        check(s.sortNumbers("nine eight seven six five four three two one zero").equals("zero one two three four five six seven eight nine"), "all number words should be mapped and sorted");
        check(s.sortNumbers("two two zero six").equals("zero two two six"), "duplicates should be preserved while sorting");    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
