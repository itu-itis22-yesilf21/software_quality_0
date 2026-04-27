/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testExactlyOne() {
        Solution s = new Solution();
        assertEquals(0.0, s.truncateNumber(1.0), 1e-6);
    }
    @Test public void testSmallDecimal() {
        Solution s = new Solution();
        assertEquals(0.000001, s.truncateNumber(1.000001), 1e-6);
    }
}