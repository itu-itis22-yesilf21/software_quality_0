import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.changeBase(5, 8).equals("5"), "x smaller than base is a single digit");
        check(s.changeBase(16, 2).equals("10000"), "exact power of base produces one followed by zeroes");
        check(s.changeBase(80, 9).equals("88"), "largest allowed base below ten is supported");    }

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