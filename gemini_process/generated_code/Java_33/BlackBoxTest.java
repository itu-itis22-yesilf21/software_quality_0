package Java_33;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testLengthThree() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 2, 3), s.sortThird(Arrays.asList(3, 2, 1)));
    }
    @Test public void testIdentical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(5, 5, 5, 5), s.sortThird(Arrays.asList(5, 5, 5, 5)));
    }
}