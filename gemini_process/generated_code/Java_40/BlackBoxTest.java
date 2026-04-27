package Java_40;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testNotQuiteZero() {
        Solution s = new Solution();
        assertFalse(s.triplesSumToZero(Arrays.asList(0, 0, 1)));
    }
}