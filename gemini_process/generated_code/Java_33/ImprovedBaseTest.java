/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_33;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testSortThirdTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 6, 3, 4, 8, 9, 5), s.sortThird(Arrays.asList(5, 6, 3, 4, 8, 9, 2)), "Sorts indices divisible by 3");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertTrue(s.sortThird(Collections.emptyList()).isEmpty(), "Empty list returns empty list");
    }
}