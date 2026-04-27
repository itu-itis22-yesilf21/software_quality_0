package Java_46;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testFour() {
        Solution s = new Solution();
        assertEquals(2, s.fib4(4));
    }
}