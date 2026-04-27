package Java_10;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testMakePalindromeTypical() {
        Solution s = new Solution();
        assertEquals("catac", s.makePalindrome("cat"), "Should append reverse of prefix");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("", s.makePalindrome(""), "Empty string remains empty");
        assertTrue(s.isPalindrome("racecar"), "Valid palindrome check");
    }
}