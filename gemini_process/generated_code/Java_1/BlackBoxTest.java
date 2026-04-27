package Java_1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testDeepNesting() {
        Solution s = new Solution();
        assertEquals(Collections.singletonList("((((()))))"), s.separateParenGroups("((((()))))"));
    }
}