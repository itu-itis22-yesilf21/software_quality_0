package Java_13;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testWithOne() {
        Solution s = new Solution();
        assertEquals(1, s.greatestCommonDivisor(1, 5));
        assertEquals(1, s.greatestCommonDivisor(5, 1));
    }
}