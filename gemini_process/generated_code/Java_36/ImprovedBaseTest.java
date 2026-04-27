/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_36;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testFizzBuzzTypical() {
        Solution s = new Solution();
        assertEquals(2, s.fizzBuzz(78), "Counts 7s up to 78");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(0, s.fizzBuzz(0), "Zero returns 0");
    }
}