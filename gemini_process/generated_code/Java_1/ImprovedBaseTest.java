/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testSeparateMultipleGroups() {
        Solution s = new Solution();
        assertEquals(Arrays.asList("()", "(())", "(()())"), s.separateParenGroups("( ) (( )) (( )( ))"), "Should separate and strip spaces");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(Collections.emptyList(), s.separateParenGroups(""), "Empty string should return empty list");
        assertEquals(Collections.emptyList(), s.separateParenGroups("   "), "String with only spaces should return empty list");
    }
}