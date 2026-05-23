/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class BookScanIntegrationTest {

    @Test
    public void testTokenisationCaseFoldingMatching() {
        BookScan scan = new BookScan();
        // Requirement 1: Multi-line text with mixed cases of the same word
        String text = "The first line\n" +
                      "the second line\n" +
                      "THE third line";
        
        Map<String, BookScan.WordStats> result = scan.scanWordsOfLength(text, 3);
        
        assertTrue(result.containsKey("the"), 
            "Requirement 1 failed: Target word not found after tokenisation and case-folding.");
            
        BookScan.WordStats stats = result.get("the");
        
        // Asserting expected behavior (3 occurrences across 3 lines) 
        // Note: This will correctly expose the bug where `flipCase` and `howManyTimes` fail on "The"
        assertEquals(3, stats.getTotalOccurrences(), 
            "Requirement 1 failed: Tokenisation x case-folding x matching did not find all mixed-case variations.");
        assertEquals(Arrays.asList(1, 2, 3), stats.getLines(), 
            "Requirement 1 failed: Did not correctly map the word to all its lines.");
    }

    @Test
    public void testTokenisationLengthFiltering() {
        BookScan scan = new BookScan();
        // Requirement 2: Text containing words of several different lengths
        String text = "a bb ccc dddd ee fff";
        
        Map<String, BookScan.WordStats> result = scan.scanWordsOfLength(text, 3);
        
        assertEquals(2, result.size(), 
            "Requirement 2 failed: Result map contains an incorrect number of words.");
        assertTrue(result.containsKey("ccc") && result.containsKey("fff"), 
            "Requirement 2 failed: Tokenisation x length filtering missed scattered words of the target length.");
        assertFalse(result.containsKey("bb") || result.containsKey("dddd"), 
            "Requirement 2 failed: Tokenisation x length filtering included words of the wrong length.");
    }

    @Test
    public void testTokenisationRepeatedWordCounting() {
        BookScan scan = new BookScan();
        // Requirement 3: Same target-length word appears more than once on a single line
        String text = "apple banana apple orange apple";
        
        Map<String, BookScan.WordStats> result = scan.scanWordsOfLength(text, 5);
        
        assertTrue(result.containsKey("apple"), 
            "Requirement 3 failed: Repeated target word not found.");
            
        BookScan.WordStats stats = result.get("apple");
        
        assertEquals(3, stats.getTotalOccurrences(), 
            "Requirement 3 failed: Tokenisation x repeated-word counting did not sum multiple occurrences on the same line correctly.");
        assertEquals(Collections.singletonList(1), stats.getLines(), 
            "Requirement 3 failed: Per-line count representation should only list the line once despite multiple occurrences.");
    }

    @Test
    public void testFlipCaseHowManyTimesIntegration() {
        BookScan scan = new BookScan();
        // Requirement 4: Case-insensitivity is the ONLY way to get the expected count
        String text = "JAVA java";
        
        Map<String, BookScan.WordStats> result = scan.scanWordsOfLength(text, 4);
        
        assertNotNull(result.get("java"), 
            "Requirement 4 failed: Target word not found.");
        assertEquals(2, result.get("java").getTotalOccurrences(), 
            "Requirement 4 failed: flipCase x howManyTimes integration failed to count both cases.");
            
        // Show that feeding only one case variant to howManyTimes gives the wrong answer
        int singleCaseCount = scan.howManyTimes(text, "java");
        assertNotEquals(2, singleCaseCount, 
            "Requirement 4 failed: Feeding only one case variant should have yielded a lower, incorrect count.");
        assertEquals(1, singleCaseCount, 
            "Requirement 4 failed: howManyTimes isolated test gave unexpected result.");
    }

    @Test
    public void testHowManyTimesSubstringOfLongerWord() {
        BookScan scan = new BookScan();
        // Requirement 5: Short target word is also a substring of a longer word on the same line
        String text = "cat concatenate";
        
        Map<String, BookScan.WordStats> result = scan.scanWordsOfLength(text, 3);
        
        assertTrue(result.containsKey("cat"), 
            "Requirement 5 failed: Word 'cat' not found.");
            
        // Assert that the integration method counts the short word exactly ONCE, not twice
        assertEquals(1, result.get("cat").getTotalOccurrences(), 
            "Requirement 5 failed: howManyTimes x substring-of-longer-word erroneously counted substrings inside longer words.");
    }

    @Test
    public void testStrlenEmptyOneCharacterWords() {
        BookScan scan = new BookScan();
        // Requirement 6: Text with one-character "words" and target length 1
        String text = "I have a pen";
        
        Map<String, BookScan.WordStats> result = scan.scanWordsOfLength(text, 1);
        
        assertTrue(result.containsKey("i") && result.containsKey("a"), 
            "Requirement 6 failed: strlen x empty / one-character words failed to identify 1-character words.");
        assertEquals(1, result.get("i").getTotalOccurrences(), 
            "Requirement 6 failed: Incorrect occurrence count for 1-character word 'i'.");
        assertEquals(1, result.get("a").getTotalOccurrences(), 
            "Requirement 6 failed: Incorrect occurrence count for 1-character word 'a'.");
    }

    @Test
    public void testEdgeCases() {
        BookScan scan = new BookScan();
        
        // Requirement 7: Empty input
        Map<String, BookScan.WordStats> emptyResult = scan.scanWordsOfLength("", 5);
        assertTrue(emptyResult.isEmpty(), 
            "Requirement 7 failed: Edge case - empty string did not return an empty map.");
            
        // Requirement 7: Null input (integration method explicitly handles nulls based on source code)
        Map<String, BookScan.WordStats> nullResult = scan.scanWordsOfLength(null, 5);
        assertTrue(nullResult.isEmpty(), 
            "Requirement 7 failed: Edge case - null input did not return an empty map.");

        // Requirement 7: Target length 0
        Map<String, BookScan.WordStats> lengthZeroResult = scan.scanWordsOfLength("some random text", 0);
        assertTrue(lengthZeroResult.isEmpty(), 
            "Requirement 7 failed: Edge case - target length 0 did not return an empty map.");

        // Requirement 7: Target length that exceeds every word in the text
        Map<String, BookScan.WordStats> exceededLengthResult = scan.scanWordsOfLength("short words only", 50);
        assertTrue(exceededLengthResult.isEmpty(), 
            "Requirement 7 failed: Edge case - target length exceeding all words did not return an empty map.");
    }
}