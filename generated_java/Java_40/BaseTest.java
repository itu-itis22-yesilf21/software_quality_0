/* @Authors
* Student Names: <Kutay Murat Kasman> <Furkan Bilal Yeşil><Ahmet Çavdar>
* Student IDs: <150210062> <150210041> <150210059>
*/

import java.util.*;
import java.lang.*;

public class BaseTest {
    public static void main(String[] args) {
        Solution s = new Solution();
        List<Boolean> correct = Arrays.asList(
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(1, 3, 5, 0))),
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(1, 3, 5, -1))),
                s.triplesSumToZero(new ArrayList<>(Arrays.asList(1, 3, -2, 1))),
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(1, 2, 3, 7))),
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(1, 2, 5, 7))),
                s.triplesSumToZero(new ArrayList<>(Arrays.asList(2, 4, -5, 3, 9, 7))),
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(1))),
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(1, 3, 5, -100))),
                !s.triplesSumToZero(new ArrayList<>(Arrays.asList(100, 3, 5, -100)))
        );
        if (correct.contains(false)) {
            throw new AssertionError();
        }
    }
}
