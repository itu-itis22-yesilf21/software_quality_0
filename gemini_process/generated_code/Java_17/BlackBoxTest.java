package Java_17;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testTrailingSpace() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2), s.parseMusic("o| "));
    }
}