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
        List<Double> linear = Arrays.asList(2.0, -4.0);
        double root = s.findZero(linear);
        check(Math.abs(s.poly(linear, root)) < 1e-5, "linear polynomial root should evaluate near zero");
        List<Double> shifted = Arrays.asList(-8.0, 0.0, 0.0, 1.0);
        double shiftedRoot = s.findZero(shifted);
        check(Math.abs(s.poly(shifted, shiftedRoot)) < 1e-5, "higher-degree polynomial root should evaluate near zero");    }

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