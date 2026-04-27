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
        check(s.longest(Arrays.asList("aa", "bb", "c")).equals(Optional.of("aa")), "ties return the first longest string");
        check(s.longest(Arrays.asList("")).equals(Optional.of("")), "single empty string is still a present value");
        check(s.longest(Arrays.asList("x", "yyyy", "zz")).equals(Optional.of("yyyy")), "longest can occur in the middle");    }

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