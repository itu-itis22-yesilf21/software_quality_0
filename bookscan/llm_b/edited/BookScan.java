/*@Authors
Student Names: <Kutay Murat Kasman><Furkan Bilal Yeşil><Ahmet Çavdar>
Student IDs:<150210062><10210041><150210059>*/
import java.util.*;
import java.lang.*;

public class BookScan {

    /**
    Find how many times a given substring can be found in the original string. Count overlapping cases.
    >>> howManyTimes("", "a")
    0
    >>> howManyTimes("aaa", "a")
    3
    >>> howManyTimes("aaaa", "aa")
    3
     */
    public int howManyTimes(String string, String substring) {
        if (string == null || string.isEmpty() || substring == null || substring.isEmpty()) {
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
        if (string == null) {
            return 0;
        }
        return string.length();
    }

    /**
    For a given string, flip lowercase characters to uppercase and uppercase to lowercase.
    >>> flipCase("Hello")
    "hELLO"
     */
    public String flipCase(String string) {
        if (string == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(string.length());
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
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

    public Map<String, List<Integer>> scan(List<String> lines, int wordLength) {
        Map<String, List<Integer>> result = new HashMap<>();
        
        if (wordLength <= 0 || lines == null) {
            return result;
        }

        int lineNumber = 1;
        for (String line : lines) {
            if (line == null) {
                line = "";
            }

            // A word is a maximal run of letters [A-Za-z]
            String[] tokens = line.split("[^A-Za-z]+");
            
            StringBuilder delimitedLine = new StringBuilder();
            delimitedLine.append('\u0001');
            
            Set<String> targetWordsInLine = new HashSet<>();

            for (String token : tokens) {
                if (token.isEmpty()) {
                    continue;
                }

                String normalizedWord = normalizeToLowercase(token);
                delimitedLine.append(normalizedWord).append('\u0001');

                if (strlen(normalizedWord) == wordLength) {
                    targetWordsInLine.add(normalizedWord);
                }
            }

            String fullDelimitedLine = delimitedLine.toString();

            // Count occurrences and record line numbers
            for (String word : targetWordsInLine) {
                String delimitedWord = "\u0001" + word + "\u0001";
                int occurrences = howManyTimes(fullDelimitedLine, delimitedWord);
                
                result.putIfAbsent(word, new ArrayList<>());
                for (int i = 0; i < occurrences; i++) {
                    result.get(word).add(lineNumber);
                }
            }

            lineNumber++;
        }

        return result;
    }

    /**
     * Normalizes a string to lowercase by strictly relying on the flipCase helper
     * for uppercase character conversion, matching the requirement constraints.
     */
    private String normalizeToLowercase(String word) {
        StringBuilder sb = new StringBuilder(strlen(word));
        for (int i = 0; i < strlen(word); i++) {
            char c = word.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(flipCase(String.valueOf(c)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}