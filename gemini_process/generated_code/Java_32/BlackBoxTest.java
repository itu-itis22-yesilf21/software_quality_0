package Java_32;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testRootAtZero() {
        Solution s = new Solution();
        assertEquals(0.0, s.findZero(Arrays.asList(0.0, 1.0)), 1e-4);
    }
}