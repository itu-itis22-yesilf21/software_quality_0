/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_12;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testLongestTypical() {
        Solution s = new Solution();
        assertEquals("ccc", s.longest(Arrays.asList("a", "bb", "ccc")).get(), "Should return longest string");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertFalse(s.longest(Collections.emptyList()).isPresent(), "Empty list returns empty Optional");
    }
}