package Java_4;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testMeanAbsoluteDeviationTypical() {
        Solution s = new Solution();
        assertEquals(1.0, s.meanAbsoluteDeviation(Arrays.asList(1.0, 2.0, 3.0, 4.0)), 1e-6, "MAD of 1,2,3,4 should be 1.0");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(0.0, s.meanAbsoluteDeviation(Collections.singletonList(5.0)), 1e-6, "MAD of single element is 0");
    }
}