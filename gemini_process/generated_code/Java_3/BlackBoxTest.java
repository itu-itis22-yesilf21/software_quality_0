package Java_3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testDropByExactlyOne() {
        Solution s = new Solution();
        assertTrue(s.belowZero(Arrays.asList(10, -11)));
    }
}