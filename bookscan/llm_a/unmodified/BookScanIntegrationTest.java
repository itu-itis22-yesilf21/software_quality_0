/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;
import java.lang.*;

public class BookScanIntegrationTest {

    public static void main(String[] args) {
        BookScanIntegrationTest test = new BookScanIntegrationTest();

        test.requirement1_tokenisationCaseFoldingMatching();
        test.requirement2_tokenisationLengthFiltering();
        test.requirement3_tokenisationRepeatedWordCounting();
        test.requirement4_flipCaseHowManyTimesCaseVariantDifference();
        test.requirement5_howManyTimesDoesNotCountSubstringInsideLongerWord();
        test.requirement6_strlenOneCharacterWords();
        test.requirement7_edgeCases();

        System.out.println("All BookScan integration tests passed.");
    }

    private void requirement1_tokenisationCaseFoldingMatching() {
        BookScan bookScan = new BookScan();

        String text = "The story starts\n"
                    + "the story continues\n"
                    + "THE story ends";

        Map<String, BookScan.WordInfo> result = bookScan.scanWords(text, 3);

        check(result.containsKey("The"),
                "Requirement 1 failed: tokenisation x case-folding x matching should include 'The'.");
        check(result.containsKey("the"),
                "Requirement 1 failed: tokenisation x case-folding x matching should include 'the'.");
        check(result.containsKey("THE"),
                "Requirement 1 failed: tokenisation x case-folding x matching should include 'THE'.");

        assertWordInfo(result, "The", 1, listOf(1),
                "Requirement 1 failed: public integration result for mixed-case word 'The' is wrong.");
        assertWordInfo(result, "the", 1, listOf(2),
                "Requirement 1 failed: public integration result for mixed-case word 'the' is wrong.");
        assertWordInfo(result, "THE", 1, listOf(3),
                "Requirement 1 failed: public integration result for mixed-case word 'THE' is wrong.");
    }

    private void requirement2_tokenisationLengthFiltering() {
        BookScan bookScan = new BookScan();

        String text = "a ox cat four\n"
                    + "dog seven sun to\n"
                    + "bird ant elephant";

        Map<String, BookScan.WordInfo> result = bookScan.scanWords(text, 3);

        Set<String> expectedWords = new LinkedHashSet<String>();
        expectedWords.add("cat");
        expectedWords.add("dog");
        expectedWords.add("sun");
        expectedWords.add("ant");

        check(result.keySet().equals(expectedWords),
                "Requirement 2 failed: tokenisation x length filtering should return only target-length words.");

        assertWordInfo(result, "cat", 1, listOf(1),
                "Requirement 2 failed: target-length word 'cat' has wrong result.");
        assertWordInfo(result, "dog", 1, listOf(2),
                "Requirement 2 failed: target-length word 'dog' has wrong result.");
        assertWordInfo(result, "sun", 1, listOf(2),
                "Requirement 2 failed: target-length word 'sun' has wrong result.");
        assertWordInfo(result, "ant", 1, listOf(3),
                "Requirement 2 failed: target-length word 'ant' has wrong result.");
    }

    private void requirement3_tokenisationRepeatedWordCounting() {
        BookScan bookScan = new BookScan();

        String text = "cat dog cat cat\n"
                    + "bird fish";

        Map<String, BookScan.WordInfo> result = bookScan.scanWords(text, 3);

        assertWordInfo(result, "cat", 3, listOf(1),
                "Requirement 3 failed: repeated target-length word should have count 3 and line list [1].");
    }

    private void requirement4_flipCaseHowManyTimesCaseVariantDifference() {
        BookScan bookScan = new BookScan();

        String text = "Cat cat CAT dog";
        Map<String, BookScan.WordInfo> result = bookScan.scanWords(text, 3);

        assertWordInfo(result, "Cat", 1, listOf(1),
                "Requirement 4 failed: public integration result for 'Cat' should count only exact case variant.");
        assertWordInfo(result, "cat", 1, listOf(1),
                "Requirement 4 failed: public integration result for 'cat' should count only exact case variant.");
        assertWordInfo(result, "CAT", 1, listOf(1),
                "Requirement 4 failed: public integration result for 'CAT' should count only exact case variant.");

        int exactCaseOnlyCount = bookScan.howManyTimes(" " + "Cat cat CAT dog" + " ", " " + "cat" + " ");
        int caseFoldedCount = bookScan.howManyTimes(" " + "cat cat cat dog" + " ", " " + "cat" + " ");

        check(exactCaseOnlyCount == 1,
                "Requirement 4 failed: feeding only one case variant to howManyTimes should give the lower exact-case count.");
        check(caseFoldedCount == 3,
                "Requirement 4 failed: case-folded input should give the expected count across case variants.");
        check(exactCaseOnlyCount != caseFoldedCount,
                "Requirement 4 failed: flipCase x howManyTimes interaction should demonstrate case variant difference.");
    }

    private void requirement5_howManyTimesDoesNotCountSubstringInsideLongerWord() {
        BookScan bookScan = new BookScan();

        String text = "cat concatenate dog";
        Map<String, BookScan.WordInfo> result = bookScan.scanWords(text, 3);

        assertWordInfo(result, "cat", 1, listOf(1),
                "Requirement 5 failed: substring inside longer word must not be counted as a separate target word.");
        check(!result.containsKey("concatenate"),
                "Requirement 5 failed: longer word containing the target substring must not appear for target length 3.");
    }

    private void requirement6_strlenOneCharacterWords() {
        BookScan bookScan = new BookScan();

        String text = "I a\n"
                    + "b C";

        Map<String, BookScan.WordInfo> result = bookScan.scanWords(text, 1);

        assertWordInfo(result, "I", 1, listOf(1),
                "Requirement 6 failed: one-character word 'I' has wrong result.");
        assertWordInfo(result, "a", 1, listOf(1),
                "Requirement 6 failed: one-character word 'a' has wrong result.");
        assertWordInfo(result, "b", 1, listOf(2),
                "Requirement 6 failed: one-character word 'b' has wrong result.");
        assertWordInfo(result, "C", 1, listOf(2),
                "Requirement 6 failed: one-character word 'C' has wrong result.");

        check(result.size() == 4,
                "Requirement 6 failed: target length 1 should return exactly the four one-character words.");
    }

    private void requirement7_edgeCases() {
        BookScan bookScan = new BookScan();

        Map<String, BookScan.WordInfo> emptyTextResult = bookScan.scanWords("", 3);
        check(emptyTextResult.isEmpty(),
                "Requirement 7 failed: empty input string should produce an empty result.");

        Map<String, BookScan.WordInfo> zeroLengthResult = bookScan.scanWords("a bb ccc", 0);
        check(zeroLengthResult.isEmpty(),
                "Requirement 7 failed: target length 0 should produce an empty result.");

        Map<String, BookScan.WordInfo> tooLargeLengthResult = bookScan.scanWords("a bb ccc", 10);
        check(tooLargeLengthResult.isEmpty(),
                "Requirement 7 failed: target length exceeding every word should produce an empty result.");
    }

    private void assertWordInfo(Map<String, BookScan.WordInfo> result,
                                String word,
                                int expectedCount,
                                List<Integer> expectedLines,
                                String failureMessage) {
        BookScan.WordInfo info = result.get(word);

        check(info != null, failureMessage + " Missing word: " + word + ".");
        check(info.getCount() == expectedCount,
                failureMessage + " Expected count " + expectedCount + " for '" + word
                + "' but got " + info.getCount() + ".");
        check(info.getLines().equals(expectedLines),
                failureMessage + " Expected lines " + expectedLines + " for '" + word
                + "' but got " + info.getLines() + ".");
    }

    private static List<Integer> listOf(int value) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(value);
        return list;
    }

    private static void check(boolean condition, String failureMessage) {
        if (!condition) {
            throw new AssertionError(failureMessage);
        }
    }
}