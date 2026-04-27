package Java_44;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testBaseNine() {
        Solution s = new Solution();
        assertEquals("8", s.changeBase(8, 9));
    }
}