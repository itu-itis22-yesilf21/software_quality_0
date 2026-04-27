package Java_20;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testFindClosestElementsTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(3.9, 4.0), s.findClosestElements(Arrays.asList(1.0, 2.0, 3.9, 4.0, 5.0, 2.2)), "Finds adjacent closest elements");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2.0, 2.0), s.findClosestElements(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 2.0)), "Handles exact duplicates");
    }
}