import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        checkClose(s.meanAbsoluteDeviation(Arrays.asList(5.0)), 0.0, "single value has zero deviation");
        checkClose(s.meanAbsoluteDeviation(Arrays.asList(2.0, 2.0, 2.0)), 0.0, "identical values have zero deviation");
        checkClose(s.meanAbsoluteDeviation(Arrays.asList(-1.0, 1.0)), 1.0, "mixed sign values use distance from mean");    }

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