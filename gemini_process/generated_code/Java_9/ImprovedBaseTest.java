package Java_9;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testRollingMaxTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 2, 3, 3, 3, 4, 4), s.rollingMax(Arrays.asList(1, 2, 3, 2, 3, 4, 2)), "Max should roll up dynamically");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertTrue(s.rollingMax(Collections.emptyList()).isEmpty(), "Empty list returns empty list");
    }
}