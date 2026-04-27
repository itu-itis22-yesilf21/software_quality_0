package Java_49;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testModpTypical() {
        Solution s = new Solution();
        assertEquals(3, s.modp(3, 5), "2^3 mod 5 is 3");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(1, s.modp(0, 101), "n=0 returns 1 (since ret initialized to 1)");
    }
}