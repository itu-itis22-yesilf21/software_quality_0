package Java_3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testBalanceDropsBelowZero() {
        Solution s = new Solution();
        assertTrue(s.belowZero(Arrays.asList(1, 2, -4, 5)), "Should return true if balance goes negative mid-operations");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertFalse(s.belowZero(Collections.emptyList()), "Empty operations list should return false");
        assertTrue(s.belowZero(Collections.singletonList(-1)), "Single negative operation should return true immediately");
    }
}