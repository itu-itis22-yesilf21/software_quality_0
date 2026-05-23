/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;
import java.lang.*;

public class BookScanIntegrationTest {

    public static void main(String[] args) {
        checkTokenisationCaseFoldingMatchingAcrossMultipleLines();
        checkTokenisationLengthFilteringOnlyTargetLengthReturned();
        checkTokenisationRepeatedWordCountingUsesPerOccurrenceLineList();
        checkFlipCaseHowManyTimesCaseInsensitiveCounting();
        checkHowManyTimesDoesNotCountSubstringInsideLongerWord();
        checkStrlenOneCharacterWords();
        checkEdgeCasesEmptyInputZeroLengthAndTooLongLength();

        System.out.println("All BookScan integration tests passed.");
    }

    private static void checkTokenisationCaseFoldingMatchingAcrossMultipleLines() {
        BookScan scanner = new BookScan();

        List<String> lines = Arrays.asList(
            "The quick brown fox",
            "jumped over the lazy dog",
            "THE end"
        );

        Map<String, List<Integer>> actual = scanner.scan(lines, 3);

        assertListEquals(
            Arrays.asList(1, 2, 3),
            actual.get("the"),
            "Requirement 1 failed: tokenisation, case-folding, and matching should count The/the/THE across multiple lines as the same word."
        );
    }

    private static void checkTokenisationLengthFilteringOnlyTargetLengthReturned() {
        BookScan scanner = new BookScan();

        List<String> lines = Arrays.asList(
            "an bird cat elephant dog",
            "fish ox cow"
        );

        Map<String, List<Integer>> actual = scanner.scan(lines, 3);

        Map<String, List<Integer>> expected = new LinkedHashMap<String, List<Integer>>();
        expected.put("cat", Arrays.asList(1));
        expected.put("dog", Arrays.asList(1));
        expected.put("cow", Arrays.asList(2));

        assertMapEquals(
            expected,
            actual,
            "Requirement 2 failed: tokenisation and length filtering should return only words whose length is exactly the target length."
        );
    }

    private static void checkTokenisationRepeatedWordCountingUsesPerOccurrenceLineList() {
        BookScan scanner = new BookScan();

        List<String> lines = Arrays.asList(
            "red blue red red green"
        );

        Map<String, List<Integer>> actual = scanner.scan(lines, 3);

        assertListEquals(
            Arrays.asList(1, 1, 1),
            actual.get("red"),
            "Requirement 3 failed: repeated target-length words should be represented by repeated line numbers, one per occurrence."
        );
    }

    private static void checkFlipCaseHowManyTimesCaseInsensitiveCounting() {
        BookScan scanner = new BookScan();

        List<String> mixedCaseLines = Arrays.asList(
            "DOG dog DoG"
        );

        Map<String, List<Integer>> actual = scanner.scan(mixedCaseLines, 3);

        assertListEquals(
            Arrays.asList(1, 1, 1),
            actual.get("dog"),
            "Requirement 4 failed: flipCase and howManyTimes should work together so DOG/dog/DoG are counted case-insensitively."
        );

        if (actual.containsKey("DOG") || actual.containsKey("DoG")) {
            throw new AssertionError(
                "Requirement 4 failed: without proper case-folding, case variants would appear as separate words instead of one normalised key."
            );
        }

        List<String> oneCaseVariantLines = Arrays.asList(
            "dog"
        );

        Map<String, List<Integer>> oneCaseOnly = scanner.scan(oneCaseVariantLines, 3);

        assertListEquals(
            Arrays.asList(1),
            oneCaseOnly.get("dog"),
            "Requirement 4 failed: feeding only one case variant gives only one occurrence, showing case-insensitive folding is necessary for the mixed-case count of three."
        );
    }

    private static void checkHowManyTimesDoesNotCountSubstringInsideLongerWord() {
        BookScan scanner = new BookScan();

        List<String> lines = Arrays.asList(
            "cat concatenate"
        );

        Map<String, List<Integer>> actual = scanner.scan(lines, 3);

        assertListEquals(
            Arrays.asList(1),
            actual.get("cat"),
            "Requirement 5 failed: howManyTimes should count the target word cat exactly once and must not count cat inside concatenate."
        );
    }

    private static void checkStrlenOneCharacterWords() {
        BookScan scanner = new BookScan();

        List<String> lines = Arrays.asList(
            "I a",
            "B see c"
        );

        Map<String, List<Integer>> actual = scanner.scan(lines, 1);

        Map<String, List<Integer>> expected = new LinkedHashMap<String, List<Integer>>();
        expected.put("i", Arrays.asList(1));
        expected.put("a", Arrays.asList(1));
        expected.put("b", Arrays.asList(2));
        expected.put("c", Arrays.asList(2));

        assertMapEquals(
            expected,
            actual,
            "Requirement 6 failed: strlen and tokenisation should include one-character words when target length is 1."
        );
    }

    private static void checkEdgeCasesEmptyInputZeroLengthAndTooLongLength() {
        BookScan scanner = new BookScan();

        assertMapEquals(
            new LinkedHashMap<String, List<Integer>>(),
            scanner.scan(new ArrayList<String>(), 3),
            "Requirement 7 failed: empty input should return an empty result."
        );

        assertMapEquals(
            new LinkedHashMap<String, List<Integer>>(),
            scanner.scan(Arrays.asList("a an the"), 0),
            "Requirement 7 failed: target length 0 should return an empty result."
        );

        assertMapEquals(
            new LinkedHashMap<String, List<Integer>>(),
            scanner.scan(Arrays.asList("a an the four"), 10),
            "Requirement 7 failed: target length exceeding every word should return an empty result."
        );
    }

    private static void assertListEquals(List<Integer> expected, List<Integer> actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                message + " Expected: " + expected + ", actual: " + actual
            );
        }
    }

    private static void assertMapEquals(
        Map<String, List<Integer>> expected,
        Map<String, List<Integer>> actual,
        String message
    ) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                message + " Expected: " + expected + ", actual: " + actual
            );
        }
    }
}