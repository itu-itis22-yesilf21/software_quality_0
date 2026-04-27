package Java_5;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testIntersperseTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 4, 2, 4, 3), s.intersperse(Arrays.asList(1, 2, 3), 4), "Should insert delimiter correctly");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertTrue(s.intersperse(Collections.emptyList(), 4).isEmpty(), "Empty list returns empty list");
        assertEquals(Collections.singletonList(10), s.intersperse(Collections.singletonList(10), 5), "Single element returns single element without delimiter");
    }
}