import java.util.*;
import java.lang.*;

public class ImprovedBaseTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.greatestCommonDivisor(0, 5) == 5, "gcd should return the non-zero value when a is zero");
        check(s.greatestCommonDivisor(5, 0) == 5, "gcd should return the non-zero value when b is zero");
        check(s.greatestCommonDivisor(9, 9) == 9, "gcd should return the value when both inputs are equal");    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
