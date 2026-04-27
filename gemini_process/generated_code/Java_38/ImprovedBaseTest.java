package Java_38;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testEncodeDecodeTypical() {
        Solution s = new Solution();
        assertEquals("abc", s.decodeCyclic(s.encodeCyclic("abc")), "Decode reverses encode perfectly");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("", s.encodeCyclic(""), "Empty string encodes to empty");
    }
}