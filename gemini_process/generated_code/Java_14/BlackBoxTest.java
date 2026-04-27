package Java_14;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testWhitespace() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(" ", " a", " a "), s.allPrefixes(" a "));
    }
}