/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/

import java.util.*;
import java.lang.*;

public class BookScan {

    /**
    Find how many times a given substring can be found in the original string. Count overlaping cases.
    >>> howManyTimes("", "a")
    0
    >>> howManyTimes("aaa", "a")
    3
    >>> howManyTimes("aaaa", "aa")
    3
     */
    public int howManyTimes(String string, String substring) {
        if (string == null || substring == null || substring.length() == 0) {
            return 0;
        }

        int count = 0;
        int substringLength = strlen(substring);

        for (int i = 0; i <= strlen(string) - substringLength; i++) {
            if (string.substring(i, i + substringLength).equals(substring)) {
                count++;
            }
        }

        return count;
    }

    /**
    Return length of given string
    >>> strlen("")
    0
    >>> strlen("abc")
    3
     */
    public int strlen(String string) {
        return string.length();
    }

    /**
    For a given string, flip lowercase characters to uppercase and uppercase to lowercase.
    >>> flipCase("Hello")
    "hELLO"
     */
    public String flipCase(String string) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < strlen(string); i++) {
            char c = string.charAt(i);

            if (Character.isLowerCase(c)) {
                result.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static class WordInfo {
        private int count;
        private final List<Integer> lines;

        public WordInfo() {
            this.count = 0;
            this.lines = new ArrayList<Integer>();
        }

        public int getCount() {
            return count;
        }

        public List<Integer> getLines() {
            return new ArrayList<Integer>(lines);
        }

        private void addOccurrence(int lineNumber) {
            count++;

            if (!lines.contains(lineNumber)) {
                lines.add(lineNumber);
            }
        }

        @Override
        public String toString() {
            return "count=" + count + ", lines=" + lines;
        }
    }

    public Map<String, WordInfo> scanWords(String text, int targetWordLength) {
        Map<String, WordInfo> result = new LinkedHashMap<String, WordInfo>();

        if (text == null || targetWordLength < 0) {
            return result;
        }

        String[] lines = text.split("\\R", -1);

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            int lineNumber = lineIndex + 1;

            String[] words = line.split("[^\\p{L}\\p{N}]+");

            for (String rawWord : words) {
                if (strlen(rawWord) == 0) {
                    continue;
                }

                String word = flipCase(flipCase(rawWord));

                if (strlen(word) == targetWordLength) {
                    WordInfo info = result.get(word);

                    if (info == null) {
                        info = new WordInfo();
                        result.put(word, info);
                    }

                    info.addOccurrence(lineNumber);
                }
            }
        }

        for (Map.Entry<String, WordInfo> entry : result.entrySet()) {
            String word = entry.getKey();
            int occurrences = howManyTimes(" " + buildWordStream(text, targetWordLength) + " ", " " + word + " ");
            entry.getValue().count = occurrences;
        }

        return result;
    }

    private String buildWordStream(String text, int targetWordLength) {
        StringBuilder stream = new StringBuilder();
        String[] words = text.split("[^\\p{L}\\p{N}]+");

        for (String rawWord : words) {
            if (strlen(rawWord) == targetWordLength) {
                if (stream.length() > 0) {
                    stream.append(" ");
                }

                stream.append(flipCase(flipCase(rawWord)));
            }
        }

        return stream.toString();
    }
}