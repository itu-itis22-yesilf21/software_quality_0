/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_0;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testCloseElementsFound() {
        Solution s = new Solution();
        assertTrue(s.hasCloseElements(Arrays.asList(1.0, 2.8, 3.0, 4.0, 5.0, 2.0), 0.3), "Should detect elements closer than threshold");
        assertTrue(s.hasCloseElements(Arrays.asList(1.1, 2.2, 3.1, 4.1, 5.1), 1.0), "Should detect elements within threshold");
    }
    @Test public void testNoCloseElements() {
        Solution s = new Solution();
        assertFalse(s.hasCloseElements(Arrays.asList(1.0, 2.0, 3.0), 0.5), "Should return false when elements are not close");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertFalse(s.hasCloseElements(Collections.emptyList(), 1.0), "Empty list should return false");
        assertFalse(s.hasCloseElements(Collections.singletonList(1.0), 1.0), "Single element list should return false");
    }
}