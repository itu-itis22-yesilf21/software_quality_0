package Java_12;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testAllEmptyStrings() {
        Solution s = new Solution();
        assertEquals("", s.longest(Arrays.asList("", "")).get());
    }
}