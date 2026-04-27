/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_5;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testTwoElements() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 5, 2), s.intersperse(Arrays.asList(1, 2), 5));
    }
    @Test public void testZeroDelimiter() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 0, 2), s.intersperse(Arrays.asList(1, 2), 0));
    }
}