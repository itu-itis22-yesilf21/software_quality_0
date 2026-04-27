package Java_23;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testStrlenTypical() {
        Solution s = new Solution();
        assertEquals(1, s.strlen("x"), "Length 1 string");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(0, s.strlen(""), "Empty string is 0");
    }
}