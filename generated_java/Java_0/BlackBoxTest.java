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
        check(!s.hasCloseElements(Arrays.asList(), 0.1), "empty list has no close pair");
        check(!s.hasCloseElements(Arrays.asList(1.0), 0.1), "single element has no close pair");
        check(!s.hasCloseElements(Arrays.asList(1.0, 1.5), 0.5), "distance equal to threshold is not close");
        check(s.hasCloseElements(Arrays.asList(-2.0, -1.95, 4.0), 0.1), "negative values can contain a close pair");    }

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