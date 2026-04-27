package Java_8;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testSumProductTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(10, 24), s.sumProduct(Arrays.asList(1, 2, 3, 4)), "Sum should be 10, Product 24");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(0, 1), s.sumProduct(Collections.emptyList()), "Empty list yields 0 sum, 1 product");
    }
}