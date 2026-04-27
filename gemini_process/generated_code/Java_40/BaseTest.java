package Java_40;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.lang.*;

public class BaseTest {
    @Test
    public void testSolution() {
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