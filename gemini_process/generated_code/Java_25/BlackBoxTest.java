package Java_25;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testFour() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 2), s.factorize(4));
    }
}