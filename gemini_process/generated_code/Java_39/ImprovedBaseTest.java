/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_39;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testPrimeFibTypical() {
        Solution s = new Solution();
        assertEquals(2, s.primeFib(1), "1st prime fib is 2");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals(433494437, s.primeFib(10), "10th prime fib checks extensive bounds");
    }
}