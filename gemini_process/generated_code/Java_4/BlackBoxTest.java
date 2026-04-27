/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_4;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testOutlier() {
        Solution s = new Solution();
        assertEquals(37.125, s.meanAbsoluteDeviation(Arrays.asList(1.0, 1.0, 1.0, 100.0)), 1e-6);
    }
}