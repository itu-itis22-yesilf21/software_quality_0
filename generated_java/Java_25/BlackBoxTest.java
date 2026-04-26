import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.factorize(1).equals(Arrays.asList()), "one has no prime factors");
        check(s.factorize(13).equals(Arrays.asList(13)), "prime input returns itself");
        check(s.factorize(16).equals(Arrays.asList(2, 2, 2, 2)), "repeated prime factor is preserved");    }

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