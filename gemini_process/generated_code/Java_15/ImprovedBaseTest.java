/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

package Java_15;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImprovedBaseTest {
    @Test public void testStringSequenceTypical() {
        Solution s = new Solution();
        assertEquals("0 1 2 3", s.stringSequence(3), "Generates sequence to 3");
    }
    @Test public void testEdgeCasesAndCoverage() {
        Solution s = new Solution();
        assertEquals("0", s.stringSequence(0), "Sequence to 0 is just 0");
    }
}