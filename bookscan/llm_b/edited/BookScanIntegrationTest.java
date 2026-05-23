/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Collections;

public class BookScanIntegrationTest {

    private final BookScan scanner = new BookScan();

    @Test
    void req1_tokenizationCaseFoldingMatching() {
        List<String> lines = List.of(
            "The quick", 
            "brown fox", 
            "jumps over the", 
            "lazy dog THE"
        );
        Map<String, List<Integer>> result = scanner.scan(lines, 3);
        
        assertTrue(result.containsKey("the"), 
            "Requirement 1 failed: 'the' should be present in the result");
            
        List<Integer> theLines = result.get("the");
        assertEquals(List.of(1, 3, 4), theLines, 
            "Requirement 1 failed: Mixed case occurrences not correctly matched across multiple lines");
    }

    @Test
    void req2_tokenizationLengthFiltering() {
        List<String> lines = List.of("a bb ccc dddd eeeee ccc fff");
        Map<String, List<Integer>> result = scanner.scan(lines, 3);
        
        assertEquals(2, result.size(), 
            "Requirement 2 failed: Should only contain distinct words of exact target length 3");
        assertTrue(result.containsKey("ccc"), 
            "Requirement 2 failed: Missing target word 'ccc'");
        assertTrue(result.containsKey("fff"), 
            "Requirement 2 failed: Missing target word 'fff'");
    }

    @Test
    void req3_tokenizationRepeatedWordCounting() {
        List<String> lines = List.of("hello world hello hello");
        Map<String, List<Integer>> result = scanner.scan(lines, 5);
        
        List<Integer> helloLines = result.get("hello");
        assertNotNull(helloLines, 
            "Requirement 3 failed: Target word 'hello' not found");
        assertEquals(List.of(1, 1, 1), helloLines, 
            "Requirement 3 failed: Repeated occurrences on the same line were not recorded correctly");
    }

    @Test
    void req4_flipCaseHowManyTimes() {
        List<String> lines = List.of("Cat CAT cAt caT");
        Map<String, List<Integer>> result = scanner.scan(lines, 3);
        
        List<Integer> catLines = result.get("cat");
        assertNotNull(catLines, "Requirement 4 failed: Target word 'cat' not found");
        assertEquals(4, catLines.size(), 
            "Requirement 4 failed: flipCase/howManyTimes integration did not correctly case-fold and count all variants");
        
        // Show that feeding only one case variant (without flipCase normalization) gives the wrong answer
        int strictLowercaseCount = scanner.howManyTimes(lines.get(0), "cat");
        assertNotEquals(strictLowercaseCount, catLines.size(), 
            "Requirement 4 failed: Case-insensitivity should be the only way to get the correct total count. Single-case count matched incorrectly.");
    }

    @Test
    void req5_howManyTimesSubstringOfLongerWord() {
        // Target length 3. "cat" is independent, "concatenate" and "tomcat" contain "cat" as a substring.
        List<String> lines = List.of("cat concatenate tomcat catatonic");
        Map<String, List<Integer>> result = scanner.scan(lines, 3);
        
        List<Integer> catLines = result.get("cat");
        assertNotNull(catLines, "Requirement 5 failed: Target word 'cat' not found");
        assertEquals(List.of(1), catLines, 
            "Requirement 5 failed: The integration method counted a short word when it was embedded inside a longer word");
    }

    @Test
    void req6_strlenEmptyOrOneCharacterWords() {
        List<String> lines = List.of("I a m a t");
        Map<String, List<Integer>> result = scanner.scan(lines, 1);
        
        assertEquals(List.of(1), result.get("i"), 
            "Requirement 6 failed: 1-char word 'i' missing or mapped incorrectly");
        assertEquals(List.of(1, 1), result.get("a"), 
            "Requirement 6 failed: 1-char word 'a' missing or mapped incorrectly");
        assertEquals(List.of(1), result.get("m"), 
            "Requirement 6 failed: 1-char word 'm' missing or mapped incorrectly");
        assertEquals(List.of(1), result.get("t"), 
            "Requirement 6 failed: 1-char word 't' missing or mapped incorrectly");
    }

    @Test
    void req7_edgeCases() {
        // Empty list
        Map<String, List<Integer>> emptyListResult = scanner.scan(Collections.emptyList(), 3);
        assertTrue(emptyListResult.isEmpty(), 
            "Requirement 7 failed: Empty input list should return an empty map");

        // Null list
        Map<String, List<Integer>> nullListResult = scanner.scan(null, 3);
        assertTrue(nullListResult.isEmpty(), 
            "Requirement 7 failed: Null input list should return an empty map");

        // Target length 0
        Map<String, List<Integer>> lengthZeroResult = scanner.scan(List.of("hello world"), 0);
        assertTrue(lengthZeroResult.isEmpty(), 
            "Requirement 7 failed: Target length 0 should return an empty map");

        // Target length that exceeds every word in the text
        Map<String, List<Integer>> exceedLengthResult = scanner.scan(List.of("a bb ccc"), 10);
        assertTrue(exceedLengthResult.isEmpty(), 
            "Requirement 7 failed: Target length exceeding all words should return an empty map");
            
        // Null line inside list
        Map<String, List<Integer>> nullLineResult = scanner.scan(Collections.singletonList(null), 3);
        assertTrue(nullLineResult.isEmpty(), 
            "Requirement 7 failed: List with null line should be handled gracefully");
    }
}