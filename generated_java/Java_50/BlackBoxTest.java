import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.decodeShift("").equals(""), "empty shifted string decodes to empty");
        check(s.decodeShift(s.encodeShift("abcxyz")).equals("abcxyz"), "shift decoder handles alphabet wraparound");
        check(s.decodeShift(s.encodeShift("z")).equals("z"), "single wraparound character round trips");    }

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