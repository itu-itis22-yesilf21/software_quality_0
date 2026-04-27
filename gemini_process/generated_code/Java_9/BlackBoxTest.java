package Java_9;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testAllSame() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 2, 2), s.rollingMax(Arrays.asList(2, 2, 2)));
    }
}