package Java_44;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testChangeBaseTypical() {
        Solution s = new Solution();
        assertEquals("22", s.changeBase(8, 3), "Base 3 of 8 is 22");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("", s.changeBase(0, 2), "0 returns empty string in this implementation");
    }
}