package Java_15;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testFive() {
        Solution s = new Solution();
        assertEquals("0 1 2 3 4 5", s.stringSequence(5));
    }
}