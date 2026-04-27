package Java_32;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testFindZeroTypical() {
        Solution s = new Solution();
        double root = s.findZero(Arrays.asList(1.0, 2.0));
        assertEquals(-0.5, root, 1e-4, "Finds exact zero for linear");
    }
    @Test public void testFindZeroEdgeCasesAndCoverage() {
        Solution s = new Solution();
        double root = s.findZero(Arrays.asList(1000.0, 1.0));
        assertTrue(Math.abs(s.poly(Arrays.asList(1000.0, 1.0), root)) < 1e-4, "Finds zero far from origin");
    }
}