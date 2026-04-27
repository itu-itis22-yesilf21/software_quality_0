/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_50;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testNonAlpha() {
        Solution s = new Solution();
        assertEquals("%", s.encodeShift(" "));
        assertEquals(" ", s.decodeShift("%"));
    }
}