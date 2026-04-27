package Java_14;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testAllPrefixesTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList("a", "ab", "abc"), s.allPrefixes("abc"), "Should return all prefixes correctly");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertTrue(s.allPrefixes("").isEmpty(), "Empty string returns empty list");
    }
}