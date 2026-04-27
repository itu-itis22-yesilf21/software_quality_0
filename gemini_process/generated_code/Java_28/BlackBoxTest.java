package Java_28;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testSpecialChars() {
        Solution s = new Solution();
        assertEquals("@#", s.concatenate(Arrays.asList("@", "#")));
    }
}