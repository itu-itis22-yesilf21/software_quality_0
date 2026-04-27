package Java_50;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testEncodeDecodeTypical() {
        Solution s = new Solution();
        String original = "hello";
        String encoded = s.encodeShift(original);
        assertEquals(original, s.decodeShift(encoded), "Decode reverses encode perfectly");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("", s.encodeShift(""), "Empty string encodes empty");
        assertEquals("abc", s.encodeShift("vwx"), "Shift that wraps end of alphabet");
    }
}