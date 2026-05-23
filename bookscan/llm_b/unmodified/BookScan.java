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
        if (string == null || substring == null || substring.isEmpty()) {
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
    Return length of given string
    >>> strlen("")
    0
    >>> strlen("abc")
    3
     */
    public int strlen(String string) {
        if (string == null) return 0;
        return string.length();
    }

    /**
    For a given string, flip lowercase characters to uppercase and uppercase to lowercase.
    >>> flipCase("Hello")
    "hELLO"
     */
    public String flipCase(String string) {
        if (string == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (Character.isLowerCase(c)) {
                sb.append(Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Given a text and a target word length, determines how many times each word 
     * of that length appears in the text and in which lines it appears.
     * 
     * @param text The text to scan
     * @param targetLength The target length of words to find
     * @return A map containing word statistics (count and line occurrences)
     */
    public Map<String, WordStats> scanWordsOfLength(String text, int targetLength) {
        Map<String, WordStats> results = new HashMap<>();
        if (text == null || text.isEmpty()) {
            return results;
        }

        String[] lines = text.split("\\r?\\n");
        Set<String> targetWords = new HashSet<>();

        // Identify all unique words in the text that match the target length
        for (String line : lines) {
            String[] words = line.split("[^a-zA-Z]+");
            for (String word : words) {
                if (strlen(word) == targetLength) {
                    targetWords.add(word.toLowerCase());
                }
            }
        }

        // Process each identified word using the required class methods
        for (String word : targetWords) {
            WordStats stats = new WordStats(word);
            String flippedWord = flipCase(word); 

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                
                // Count occurrences case-insensitively using howManyTimes on standard and flipped case
                int standardCount = howManyTimes(line, word);
                int flippedCount = howManyTimes(line, flippedWord);
                
                int totalLineOccurrences = standardCount + flippedCount;

                if (totalLineOccurrences > 0) {
                    stats.addOccurrences(totalLineOccurrences);
                    stats.addLine(i + 1); // 1-based line counting
                }
            }

            if (stats.getTotalOccurrences() > 0) {
                results.put(word, stats);
            }
        }

        return results;
    }

    public static class WordStats {
        private String word;
        private int totalOccurrences;
        private List<Integer> lines;

        public WordStats(String word) {
            this.word = word;
            this.totalOccurrences = 0;
            this.lines = new ArrayList<>();
        }

        public void addOccurrences(int count) {
            this.totalOccurrences += count;
        }

        public void addLine(int lineNumber) {
            if (!this.lines.contains(lineNumber)) {
                this.lines.add(lineNumber);
            }
        }

        public String getWord() {
            return word;
        }

        public int getTotalOccurrences() {
            return totalOccurrences;
        }

        public List<Integer> getLines() {
            return lines;
        }

        @Override
        public String toString() {
            return "Count: " + totalOccurrences + ", Lines: " + lines;
        }
    }
}