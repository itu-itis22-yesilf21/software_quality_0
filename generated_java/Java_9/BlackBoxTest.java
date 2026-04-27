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
        check(s.rollingMax(Arrays.asList()).equals(Arrays.asList()), "empty list has empty rolling maximum");
        check(s.rollingMax(Arrays.asList(5, 4, 3)).equals(Arrays.asList(5, 5, 5)), "descending sequence keeps first maximum");
        check(s.rollingMax(Arrays.asList(-3, -2, -5)).equals(Arrays.asList(-3, -2, -2)), "negative values still track the highest value");    }

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