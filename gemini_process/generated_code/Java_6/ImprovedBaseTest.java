package Java_6;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testNestedParensTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 3, 1, 3), s.parseNestedParens("(()()) ((())) () ((())()())"), "Should calculate correct max depths per group");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(Collections.emptyList(), s.parseNestedParens(""), "Empty string returns empty list");
    }
}