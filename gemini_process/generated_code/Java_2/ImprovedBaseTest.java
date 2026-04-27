/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testTypicalDecimals() {
        Solution s = new Solution();
        assertEquals(0.5, s.truncateNumber(3.5), 1e-6, "Should extract 0.5 from 3.5");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(0.0, s.truncateNumber(0.0), 1e-6, "Zero should return zero");
        assertEquals(0.0, s.truncateNumber(5.0), 1e-6, "Whole number should return zero decimal part");
    }
}