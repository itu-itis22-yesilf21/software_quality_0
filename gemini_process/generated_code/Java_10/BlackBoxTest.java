package Java_10;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testOddLengthPartial() {
        Solution s = new Solution();
        assertEquals("abacaba", s.makePalindrome("abac"));
    }
}