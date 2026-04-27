/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_0;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testZeroThreshold() {
        Solution s = new Solution();
        assertFalse(s.hasCloseElements(Arrays.asList(1.0, 1.0), 0.0));
    }
    @Test public void testNegativeThreshold() {
        Solution s = new Solution();
        assertFalse(s.hasCloseElements(Arrays.asList(1.0, 1.2), -0.1));
    }
}