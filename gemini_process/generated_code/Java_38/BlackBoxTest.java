package Java_38;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testSpecialChars() {
        Solution s = new Solution();
        assertEquals("#$@", s.encodeCyclic("@#$"));
        assertEquals("@#$", s.decodeCyclic("#$@"));
    }
}