/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_40;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testTriplesSumToZeroFound() {
        Solution s = new Solution();
        assertTrue(s.triplesSumToZero(Arrays.asList(1, 3, -2, 1)), "1, -2, 1 sums to 0");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertFalse(s.triplesSumToZero(Collections.emptyList()), "Empty list returns false");
    }
}