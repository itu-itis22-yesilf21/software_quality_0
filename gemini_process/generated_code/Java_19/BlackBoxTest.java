package Java_19;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testExtraSpaces() {
        Solution s = new Solution();
        assertEquals("one two", s.sortNumbers("two one"));
    }
}