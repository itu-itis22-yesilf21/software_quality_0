/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;
import java.lang.*;

public class BookScan {

    /**
    Find how many times a given substring can be found in the original string.
    Count overlapping cases.
      howManyTimes("", "a")     -> 0
      howManyTimes("aaa", "a")  -> 3
      howManyTimes("aaaa", "aa") -> 3
     */
    public int howManyTimes(String string, String substring) {
        if (string == null || substring == null || substring.length() == 0) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i <= string.length() - substring.length(); i++) {
            if (string.substring(i, i + substring.length()).equals(substring)) {
                count++;
            }
        }

        return count;
    }

    /**
    Return the length of the given string.
      strlen("")    -> 0
      strlen("abc") -> 3
     */
    public int strlen(String string) {
        return string.length();
    }

    /**
    Flip every lowercase letter to uppercase and every uppercase letter
    to lowercase. Non-letter characters are unchanged.
      flipCase("Hello") -> "hELLO"
     */
    public String flipCase(String string) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);

            if (c >= 'a' && c <= 'z') {
                result.append((char) (c - 'a' + 'A'));
            } else if (c >= 'A' && c <= 'Z') {
                result.append((char) (c - 'A' + 'a'));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public Map<String, List<Integer>> scan(List<String> lines, int wordLength) {
        Map<String, List<Integer>> result = new LinkedHashMap<String, List<Integer>>();

        if (lines == null || wordLength <= 0) {
            return result;
        }

        final String delimiter = "\u0001";

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);

            if (line == null) {
                line = "";
            }

            List<String> wordsInLine = extractNormalisedWords(line);
            StringBuilder normalisedLine = new StringBuilder();
            Set<String> targetWordsInLine = new LinkedHashSet<String>();

            for (String word : wordsInLine) {
                normalisedLine.append(delimiter).append(word).append(delimiter);

                if (strlen(word) == wordLength) {
                    targetWordsInLine.add(word);

                    if (!result.containsKey(word)) {
                        result.put(word, new ArrayList<Integer>());
                    }
                }
            }

            for (String word : targetWordsInLine) {
                int occurrences = howManyTimes(
                    normalisedLine.toString(),
                    delimiter + word + delimiter
                );

                List<Integer> lineNumbers = result.get(word);

                for (int i = 0; i < occurrences; i++) {
                    lineNumbers.add(lineIndex + 1);
                }
            }
        }

        return result;
    }

    private List<String> extractNormalisedWords(String line) {
        List<String> words = new ArrayList<String>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (isAsciiLetter(c)) {
                currentWord.append(c);
            } else if (currentWord.length() > 0) {
                words.add(normalise(currentWord.toString()));
                currentWord.setLength(0);
            }
        }

        if (currentWord.length() > 0) {
            words.add(normalise(currentWord.toString()));
        }

        return words;
    }

    private String normalise(String word) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < strlen(word); i++) {
            String character = word.substring(i, i + 1);

            if (character.compareTo("A") >= 0 && character.compareTo("Z") <= 0) {
                result.append(flipCase(character));
            } else {
                result.append(character);
            }
        }

        return result.toString();
    }

    private boolean isAsciiLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}