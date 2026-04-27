package Java_8;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testLargeNumbers() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2000, 1000000), s.sumProduct(Arrays.asList(1000, 1000)));
    }
}