/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(!s.triplesSumToZero(Arrays.asList()), "empty list has no triple");
        check(s.triplesSumToZero(Arrays.asList(0, 0, 0)), "three zeroes form a distinct zero-sum triple");
        check(s.triplesSumToZero(Arrays.asList(-1, 0, 1)), "exactly three values can form zero");    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void checkClose(double actual, double expected, String message) {
        if (Math.abs(actual - expected) > 1e-6) {
            throw new AssertionError(message + " expected=" + expected + " actual=" + actual);
        }
    }
}