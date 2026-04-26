import java.util.*;
import java.lang.*;

public class ImprovedBaseTest {
    public static void main(String[] args) {
        BaseTest.main(args);
        Solution s = new Solution();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
