import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.sortThird(Arrays.asList()).equals(Arrays.asList()), "empty list remains empty");
        check(s.sortThird(Arrays.asList(9, 1, 2, 3)).equals(Arrays.asList(3, 1, 2, 9)), "indices divisible by three are sorted together");
        check(s.sortThird(Arrays.asList(4, 8)).equals(Arrays.asList(4, 8)), "short lists without multiple third positions remain stable");    }

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