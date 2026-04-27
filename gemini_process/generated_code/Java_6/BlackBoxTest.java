package Java_6;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testDepthOneRepeated() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 1), s.parseNestedParens("() ()"));
    }
}