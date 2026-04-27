package Java_17;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testParseMusicTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(4, 2, 1), s.parseMusic("o o| .|"), "Parses mixed notes correctly");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertTrue(s.parseMusic("").isEmpty(), "Empty string returns empty list");
    }
}