import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.fizzBuzz(0) == 0, "n=0 has no numbers to inspect");
        check(s.fizzBuzz(77) == 0, "upper bound is exclusive and excludes 77");
        check(s.fizzBuzz(78) == 2, "including 77 contributes two digit sevens");    }

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