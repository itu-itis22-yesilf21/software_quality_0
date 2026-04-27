package Java_50;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testNonAlpha() {
        Solution s = new Solution();
        assertEquals("%", s.encodeShift(" "));
        assertEquals(" ", s.decodeShift("%"));
    }
}