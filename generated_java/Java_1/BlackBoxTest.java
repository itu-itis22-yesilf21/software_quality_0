import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.separateParenGroups("").equals(Arrays.asList()), "empty parenthesis string has no groups");
        check(s.separateParenGroups("()()").equals(Arrays.asList("()", "()")), "adjacent groups are separated without spaces");
        check(s.separateParenGroups(" ((())) ").equals(Arrays.asList("((()))")), "outer spaces are ignored");    }

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