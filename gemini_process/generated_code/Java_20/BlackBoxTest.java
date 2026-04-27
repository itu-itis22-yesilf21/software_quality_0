package Java_20;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testSymmetricDistances() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1.0, 3.0), s.findClosestElements(Arrays.asList(1.0, 3.0, 5.0)));
    }
}