package Java_36;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testLimitThirteen() {
        Solution s = new Solution();
        assertEquals(0, s.fizzBuzz(13));
    }
}