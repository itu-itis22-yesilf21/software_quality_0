/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_20;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testSymmetricDistances() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1.0, 3.0), s.findClosestElements(Arrays.asList(1.0, 3.0, 5.0)));
    }
}