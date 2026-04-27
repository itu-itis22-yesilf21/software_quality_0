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
        check(s.sumProduct(Arrays.asList(5)).equals(Arrays.asList(5, 5)), "single value is both sum and product");
        check(s.sumProduct(Arrays.asList(0, 4, 5)).equals(Arrays.asList(9, 0)), "zero drives product to zero");
        check(s.sumProduct(Arrays.asList(-1, 2, -3)).equals(Arrays.asList(-2, 6)), "negative factors affect sign correctly");    }

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