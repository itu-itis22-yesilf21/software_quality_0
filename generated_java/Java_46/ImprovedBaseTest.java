import java.util.*;
import java.lang.*;

public class ImprovedBaseTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
        check(s.fib4(0) == 0, "fib4 base case n=0");
        check(s.fib4(1) == 0, "fib4 base case n=1");
        check(s.fib4(2) == 2, "fib4 base case n=2");
        check(s.fib4(3) == 0, "fib4 base case n=3");    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
