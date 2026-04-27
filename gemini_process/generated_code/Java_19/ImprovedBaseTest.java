package Java_19;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testSortNumbersTypical() {
        Solution s = new Solution();
        assertEquals("one three five", s.sortNumbers("three one five"), "Sorts multiple valid number strings");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("", s.sortNumbers(""), "Empty string returns empty");
        assertEquals("zero one two three four five six seven eight nine", s.sortNumbers("nine eight seven six five four three two one zero"), "Covers all valid mapping branches");
    }
}