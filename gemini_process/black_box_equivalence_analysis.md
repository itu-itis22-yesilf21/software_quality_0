# Black Box Equivalence Analysis

## Task ID: Java_0

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty List | Valid | [], 0.5 | false | Yes |
| Single Element | Valid | [1.0], 0.5 | false | Yes |
| Two close elements | Valid | [1.0, 1.2], 0.5 | true | Yes |
| Threshold Zero | Boundary | [1.0, 1.0], 0.0 | false | No |
| Threshold Negative | Boundary | [1.0, 1.2], -0.1 | false | No |

### Assessment Summary:
Base tests cover basic functionality but miss extreme boundary conditions like threshold = 0.0 or negative thresholds.

### File Path: gemini_process/generated_code/Java_0/BlackBoxTest.java

```java
package Java_0;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testZeroThreshold() {
        Solution s = new Solution();
        assertFalse(s.hasCloseElements(Arrays.asList(1.0, 1.0), 0.0));
    }
    @Test public void testNegativeThreshold() {
        Solution s = new Solution();
        assertFalse(s.hasCloseElements(Arrays.asList(1.0, 1.2), -0.1));
    }
}
```

## Task ID: Java_1

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty String | Valid | "" | [] | Yes |
| Spaces Only | Valid | "   " | [] | Yes |
| Simple Pair | Valid | "()" | ["()"] | Yes |
| Deep Nesting | Boundary | "((((()))))" | ["((((()))))"] | No |

### Assessment Summary:
Base tests cover standard parsing well, but we should explicitly test extremely deep nesting boundary.

### File Path: gemini_process/generated_code/Java_1/BlackBoxTest.java

```java
package Java_1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testDeepNesting() {
        Solution s = new Solution();
        assertEquals(Collections.singletonList("((((()))))"), s.separateParenGroups("((((()))))"));
    }
}
```

## Task ID: Java_2

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Zero | Valid | 0.0 | 0.0 | Yes |
| Positive Decimal | Valid | 3.5 | 0.5 | Yes |
| Exactly One | Boundary | 1.0 | 0.0 | No |
| Very Small Decimal | Boundary | 1.000001 | 0.000001 | No |

### Assessment Summary:
Base tests cover basic float extraction but we should test boundary of exactly 1.0 and extremely small fractions.

### File Path: gemini_process/generated_code/Java_2/BlackBoxTest.java

```java
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
```

## Task ID: Java_3

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty List | Valid | [] | false | Yes |
| Negative first | Valid | [-1] | true | Yes |
| Drops by exactly 1 | Boundary | [10, -11] | true | No |

### Assessment Summary:
Base test misses extremely large inputs or boundary where it drops below zero by exactly 1.

### File Path: gemini_process/generated_code/Java_3/BlackBoxTest.java

```java
package Java_3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testDropByExactlyOne() {
        Solution s = new Solution();
        assertTrue(s.belowZero(Arrays.asList(10, -11)));
    }
}
```

## Task ID: Java_4

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Typical | Valid | [1,2,3,4] | 1.0 | Yes |
| Outlier | Valid | [1, 1, 1, 100] | 37.125 | No |
| Same numbers | Valid | [5,5] | 0.0 | Yes |

### Assessment Summary:
Base tests miss single extreme outlier cases and extremely large decimals.

### File Path: gemini_process/generated_code/Java_4/BlackBoxTest.java

```java
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
```

## Task ID: Java_5

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty list | Valid | [], 4 | [] | Yes |
| Two elements | Boundary | [1,2], 5 | [1,5,2] | No |
| Zero delimiter | Valid | [1,2], 0 | [1,0,2] | No |

### Assessment Summary:
Base tests miss empty string delimiters or edge case with two elements.

### File Path: gemini_process/generated_code/Java_5/BlackBoxTest.java

```java
package Java_5;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testTwoElements() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 5, 2), s.intersperse(Arrays.asList(1, 2), 5));
    }
    @Test public void testZeroDelimiter() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 0, 2), s.intersperse(Arrays.asList(1, 2), 0));
    }
}
```

## Task ID: Java_6

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty | Valid | "" | [] | Yes |
| Depth 1 | Valid | "() ()" | [1, 1] | No |
| Complex | Valid | "(())" | [2] | Yes |

### Assessment Summary:
Misses edge cases like max nesting depths of 1 repeatedly.

### File Path: gemini_process/generated_code/Java_6/BlackBoxTest.java

```java
package Java_6;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testDepthOneRepeated() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 1), s.parseNestedParens("() ()"));
    }
}
```

## Task ID: Java_8

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty | Valid | [] | [0, 1] | Yes |
| One | Boundary | [1] | [1, 1] | Yes |
| Large numbers | Boundary | [1000, 1000] | [2000, 1000000] | No |

### Assessment Summary:
Misses extremely large numbers leading to overflow, though returning int limits it.

### File Path: gemini_process/generated_code/Java_8/BlackBoxTest.java

```java
package Java_8;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testLargeNumbers() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2000, 1000000), s.sumProduct(Arrays.asList(1000, 1000)));
    }
}
```

## Task ID: Java_9

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty | Valid | [] | [] | Yes |
| All same | Boundary | [2,2,2] | [2,2,2] | No |
| Normal | Valid | [1,3,2] | [1,3,3] | Yes |

### Assessment Summary:
Base test misses lists with all same elements.

### File Path: gemini_process/generated_code/Java_9/BlackBoxTest.java

```java
package Java_9;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testAllSame() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 2, 2), s.rollingMax(Arrays.asList(2, 2, 2)));
    }
}
```

## Task ID: Java_10

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Empty | Valid | "" | "" | Yes |
| Odd length partial | Boundary | "abac" | "abacaba" | No |
| Even length partial | Valid | "cata" | "catac" | Yes |

### Assessment Summary:
Misses edge cases of odd length partial palindromes.

### File Path: gemini_process/generated_code/Java_10/BlackBoxTest.java

```java
package Java_10;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testOddLengthPartial() {
        Solution s = new Solution();
        assertEquals("abacaba", s.makePalindrome("abac"));
    }
}
```

## Task ID: Java_12

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| All empty strings | Boundary | ["", ""] | "" | No |
| Normal | Valid | ["a", "bb"] | "bb" | Yes |

### Assessment Summary:
Misses lists with many empty strings.

### File Path: gemini_process/generated_code/Java_12/BlackBoxTest.java

```java
package Java_12;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testAllEmptyStrings() {
        Solution s = new Solution();
        assertEquals("", s.longest(Arrays.asList("", "")).get());
    }
}
```

## Task ID: Java_13

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| With One | Boundary | 1, 5 | 1 | No |
| With Self | Boundary | 5, 5 | 5 | Yes |

### Assessment Summary:
Misses inputs of 1.

### File Path: gemini_process/generated_code/Java_13/BlackBoxTest.java

```java
package Java_13;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testWithOne() {
        Solution s = new Solution();
        assertEquals(1, s.greatestCommonDivisor(1, 5));
        assertEquals(1, s.greatestCommonDivisor(5, 1));
    }
}
```

## Task ID: Java_14

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Whitespaces | Valid | " a " | [" ", " a", " a "] | No |
| Normal | Valid | "abc" | ["a", "ab", "abc"] | Yes |

### Assessment Summary:
Misses prefixes with whitespaces.

### File Path: gemini_process/generated_code/Java_14/BlackBoxTest.java

```java
package Java_14;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testWhitespace() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(" ", " a", " a "), s.allPrefixes(" a "));
    }
}
```

## Task ID: Java_15

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Zero | Boundary | 0 | "0" | Yes |
| Five | Valid | 5 | "0 1 2 3 4 5" | No |

### Assessment Summary:
Misses testing negative numbers (if input type was unrestricted, but it expects >=0).

### File Path: gemini_process/generated_code/Java_15/BlackBoxTest.java

```java
package Java_15;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testFive() {
        Solution s = new Solution();
        assertEquals("0 1 2 3 4 5", s.stringSequence(5));
    }
}
```

## Task ID: Java_17

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Trailing Space | Boundary | "o| " | [2] | No |
| Normal | Valid | "o o|" | [4, 2] | Yes |

### Assessment Summary:
Misses trailing spaces or isolated notes.

### File Path: gemini_process/generated_code/Java_17/BlackBoxTest.java

```java
package Java_17;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testTrailingSpace() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2), s.parseMusic("o| "));
    }
}
```

## Task ID: Java_19

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Extra spaces | Boundary | "one   two" | "one two" | No |
| Normal | Valid | "two one" | "one two" | Yes |

### Assessment Summary:
Misses strings with lots of whitespace.

### File Path: gemini_process/generated_code/Java_19/BlackBoxTest.java

```java
package Java_19;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testExtraSpaces() {
        Solution s = new Solution();
        assertEquals("one two", s.sortNumbers("two one"));
    }
}
```

## Task ID: Java_20

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Symmetric distances | Boundary | [1.0, 3.0, 5.0] | [1.0, 3.0] | No |
| Normal | Valid | [1.0, 2.0, 3.9, 4.0] | [3.9, 4.0] | Yes |

### Assessment Summary:
Misses exactly same distance ties where elements are identical or symmetric.

### File Path: gemini_process/generated_code/Java_20/BlackBoxTest.java

```java
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
```

## Task ID: Java_23

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Special chars | Valid | "@#$" | 3 | No |
| Normal | Valid | "abc" | 3 | Yes |

### Assessment Summary:
Misses extremely long strings or special characters.

### File Path: gemini_process/generated_code/Java_23/BlackBoxTest.java

```java
package Java_23;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testSpecialChars() {
        Solution s = new Solution();
        assertEquals(3, s.strlen("@#$"));
    }
}
```

## Task ID: Java_25

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Four | Boundary | 4 | [2, 2] | No |
| Eight | Valid | 8 | [2, 2, 2] | Yes |

### Assessment Summary:
Misses small non-prime boundary like 4.

### File Path: gemini_process/generated_code/Java_25/BlackBoxTest.java

```java
package Java_25;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testFour() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(2, 2), s.factorize(4));
    }
}
```

## Task ID: Java_28

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Special chars | Valid | ["@", "#"] | "@#" | No |
| Normal | Valid | ["a", "b"] | "ab" | Yes |

### Assessment Summary:
Misses strings with special characters.

### File Path: gemini_process/generated_code/Java_28/BlackBoxTest.java

```java
package Java_28;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testSpecialChars() {
        Solution s = new Solution();
        assertEquals("@#", s.concatenate(Arrays.asList("@", "#")));
    }
}
```

## Task ID: Java_32

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Root at Zero | Boundary | [0.0, 1.0] | 0.0 | No |
| Normal | Valid | [1.0, 2.0] | -0.5 | Yes |

### Assessment Summary:
Misses roots at 0.0.

### File Path: gemini_process/generated_code/Java_32/BlackBoxTest.java

```java
package Java_32;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testRootAtZero() {
        Solution s = new Solution();
        assertEquals(0.0, s.findZero(Arrays.asList(0.0, 1.0)), 1e-4);
    }
}
```

## Task ID: Java_33

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Length 3 | Boundary | [3, 2, 1] | [1, 2, 3] | No |
| Identical | Boundary | [5, 5, 5, 5] | [5, 5, 5, 5] | No |

### Assessment Summary:
Misses lists of length exactly 3 or all identical values.

### File Path: gemini_process/generated_code/Java_33/BlackBoxTest.java

```java
package Java_33;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testLengthThree() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(1, 2, 3), s.sortThird(Arrays.asList(3, 2, 1)));
    }
    @Test public void testIdentical() {
        Solution s = new Solution();
        assertEquals(Arrays.asList(5, 5, 5, 5), s.sortThird(Arrays.asList(5, 5, 5, 5)));
    }
}
```

## Task ID: Java_36

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Limit 13 | Boundary | 13 | 0 | No |
| Normal | Valid | 78 | 2 | Yes |

### Assessment Summary:
Misses exactly limits of 13.

### File Path: gemini_process/generated_code/Java_36/BlackBoxTest.java

```java
package Java_36;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testLimitThirteen() {
        Solution s = new Solution();
        assertEquals(0, s.fizzBuzz(13));
    }
}
```

## Task ID: Java_38

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Length 3 | Boundary | "abc" | "bca" | Yes |
| Special Chars | Valid | "@#$" | "#$@" | No |

### Assessment Summary:
Misses strings exactly length 3.

### File Path: gemini_process/generated_code/Java_38/BlackBoxTest.java

```java
package Java_38;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testSpecialChars() {
        Solution s = new Solution();
        assertEquals("#$@", s.encodeCyclic("@#$"));
        assertEquals("@#$", s.decodeCyclic("#$@"));
    }
}
```

## Task ID: Java_39

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| n = 4 | Valid | 4 | 13 | No |
| n = 1 | Boundary | 1 | 2 | Yes |

### Assessment Summary:
Misses large fibonacci primes where overflow might occur.

### File Path: gemini_process/generated_code/Java_39/BlackBoxTest.java

```java
package Java_39;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testFour() {
        Solution s = new Solution();
        assertEquals(13, s.primeFib(4));
    }
}
```

## Task ID: Java_40

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Zeroes | Boundary | [0, 0, 0] | true | Yes |
| Not quite zero | Boundary | [0, 0, 1] | false | No |

### Assessment Summary:
Misses triplets containing zeroes.

### File Path: gemini_process/generated_code/Java_40/BlackBoxTest.java

```java
package Java_40;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
public class BlackBoxTest {
    @Test public void testNotQuiteZero() {
        Solution s = new Solution();
        assertFalse(s.triplesSumToZero(Arrays.asList(0, 0, 1)));
    }
}
```

## Task ID: Java_44

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Base 9 | Boundary | 8, 9 | "8" | No |
| Base 2 | Boundary | 8, 2 | "1000" | Yes |

### Assessment Summary:
Misses converting to max base 9.

### File Path: gemini_process/generated_code/Java_44/BlackBoxTest.java

```java
package Java_44;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testBaseNine() {
        Solution s = new Solution();
        assertEquals("8", s.changeBase(8, 9));
    }
}
```

## Task ID: Java_46

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Four | Boundary | 4 | 2 | No |
| Five | Valid | 5 | 4 | Yes |

### Assessment Summary:
Misses exactly 4.

### File Path: gemini_process/generated_code/Java_46/BlackBoxTest.java

```java
package Java_46;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testFour() {
        Solution s = new Solution();
        assertEquals(2, s.fib4(4));
    }
}
```

## Task ID: Java_49

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Power 1 | Boundary | 1, 5 | 2 | No |
| Normal | Valid | 3, 5 | 3 | Yes |

### Assessment Summary:
Misses power of 1.

### File Path: gemini_process/generated_code/Java_49/BlackBoxTest.java

```java
package Java_49;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BlackBoxTest {
    @Test public void testPowerOne() {
        Solution s = new Solution();
        assertEquals(2, s.modp(1, 5));
    }
}
```

## Task ID: Java_50

### Manual Assessment Table:

| Class/Boundary | Input Type | Input Value | Expected Output | Covered by Base Test |
|---|---|---|---|---|
| Non-alpha | Boundary | " " | " %" | No |
| Alpha | Valid | "a" | "f" | Yes |

### Assessment Summary:
Misses non-alphabetic characters as a boundary.

### File Path: gemini_process/generated_code/Java_50/BlackBoxTest.java

```java
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
```

