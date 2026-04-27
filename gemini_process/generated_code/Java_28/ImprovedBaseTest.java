/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_28;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testConcatenateTypical() {
        Solution s = new Solution();
        assertEquals("xyz", s.concatenate(Arrays.asList("x", "y", "z")), "Concatenates multiple single chars");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("", s.concatenate(Collections.emptyList()), "Empty list returns empty string");
    }
}