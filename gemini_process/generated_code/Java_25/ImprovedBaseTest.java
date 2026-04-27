/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_25;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class ImprovedBaseTest {
    @Test public void testFactorizeTypical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 2, 2), s.factorize(8), "8 factorizes to 2,2,2");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertTrue(s.factorize(1).isEmpty(), "1 has no prime factors in this context");
    }
}