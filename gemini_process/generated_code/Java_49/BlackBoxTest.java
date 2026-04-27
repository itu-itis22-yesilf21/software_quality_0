package Java_49;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testPowerOne() {
        Solution s = new Solution();
        assertEquals(2, s.modp(1, 5));
    }
}