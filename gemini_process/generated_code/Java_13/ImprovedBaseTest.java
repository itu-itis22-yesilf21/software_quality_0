/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_13;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testGCDTypical() {
        Solution s = new Solution();
        assertEquals(5, s.greatestCommonDivisor(10, 15), "GCD of 10 and 15 is 5");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(10, s.greatestCommonDivisor(10, 0), "a > b where b=0 returns a");
    }
}