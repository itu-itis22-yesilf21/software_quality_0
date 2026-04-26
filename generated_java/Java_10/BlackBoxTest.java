import java.util.*;
import java.lang.*;

public class BlackBoxTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.makePalindrome("racecar").equals("racecar"), "existing palindrome remains unchanged");
        check(s.makePalindrome("ab").equals("aba"), "two-character non-palindrome appends first char");
        check(s.makePalindrome("abab").equals("ababa"), "palindromic suffix is reused");    }

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