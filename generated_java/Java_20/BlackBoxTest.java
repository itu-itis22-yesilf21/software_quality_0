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
        check(s.findClosestElements(Arrays.asList(5.0, 1.0)).equals(Arrays.asList(1.0, 5.0)), "two elements are returned in sorted order");
        check(s.findClosestElements(Arrays.asList(-1.0, -1.5, 2.0)).equals(Arrays.asList(-1.5, -1.0)), "closest pair can be negative");
        check(s.findClosestElements(Arrays.asList(3.0, 3.0, 10.0)).equals(Arrays.asList(3.0, 3.0)), "duplicate values are the closest possible pair");    }

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