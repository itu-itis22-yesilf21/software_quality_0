# Phase 2 / Step 3 — BookScan Class Generation Log

This log captures every LLM interaction that produced a `BookScan.java`
variant. Format mirrors Phase 1's `gemini_process/logs/code_generation_log.md`.

Each section records:

1. **Prompt sent** — verbatim, copy-pasted from
   `bookscan/logs/prompt_design_log.md` (no modifications, no system
   prompt additions beyond what the hosted UI adds by default).
2. **Model + access details** — model label, UI used, date, any
   non-default sampling parameters.
3. **Response received** — verbatim, including any prose the model added
   outside the requested fenced code block.
4. **How the output was used** — saved path, any post-processing (e.g.,
   stripping a leading markdown fence, fixing the `@Authors` placeholder).

The four runs in this step are:

| # | Prompt variant | LLM | Save path |
|---|---|---|---|
| 1 | Variant 1 (unmodified+combined) | LLM A — GPT-5.5         | `bookscan/llm_a/unmodified/BookScan.java` |
| 2 | Variant 1 (unmodified+combined) | LLM B — Gemini 3.1 Pro  | `bookscan/llm_b/unmodified/BookScan.java` |
| 3 | Variant 2 (edited+combined)     | LLM A — GPT-5.5         | `bookscan/llm_a/edited/BookScan.java`     |
| 4 | Variant 2 (edited+combined)     | LLM B — Gemini 3.1 Pro  | `bookscan/llm_b/edited/BookScan.java`     |

Each run gets its own step-tagged commit (`Phase 2 / Step 3.<run#>: ...`).

---

## Run 1 — Variant 1 → GPT-5.5  *(complete)*

**Prompt sent.** The exact text under "Variant 1 — Unmodified + Combined
prompt" in `bookscan/logs/prompt_design_log.md` (committed at `8a1982f`),
copied verbatim into the GPT-5.5 web UI as a single message with no
system-prompt additions.

**Model + access details.** GPT-5.5 via its hosted chat UI, default
sampling, run on 2026-05-24. Sent as a single turn; no follow-up prompts.

**Response received.** Verbatim Java source reproduced below. The model
returned only the source (no surrounding prose). The first paste into
`bookscan/logs/response.txt` had four `*` characters stripped by the
browser's markdown-aware copy (header `/*…*/` lines and the two
`import …*;` lines); the user re-pasted using the GPT-5.5 "Copy code"
button on the code block and the second paste preserved the asterisks.
The saved file matches the second paste byte-for-byte.

```java
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
```

**How the output was used.** Saved verbatim to
`bookscan/llm_a/unmodified/BookScan.java` (no edits beyond the asterisk
restoration noted above). The transient paste-buffer file
`bookscan/logs/response.txt` was deleted in the same commit.

**First observations (data for the Step 11 LLM-comparison table).**
The unmodified prompt left several integration details open, and GPT-5.5
filled them in as follows:

| Decision | What GPT-5.5 chose | Notes |
|---|---|---|
| Public API name | `scanWords(String text, int targetWordLength)` | Single-string text, not `List<String>`; splits internally on `\R`. |
| Output type | `Map<String, WordInfo>` with `WordInfo { count, lines }` | Invented its own record type; not directly comparable to the `Map<String,List<Integer>>` shape pinned by Variant 2. |
| Tokenisation | `split("[^\\p{L}\\p{N}]+")` | Unicode letters or digits as word chars (different from the `[A-Za-z]`-only rule pinned by Variant 2). |
| Case handling | `flipCase(flipCase(rawWord))` — identity | The double-flip is a no-op, so matching is effectively case-sensitive. The model called `flipCase` but its output does not change normalisation. |
| `howManyTimes` integration | Re-counts via `howManyTimes(" "+stream+" ", " "+word+" ")` over a `buildWordStream` of all length-`k` words | Overrides the per-line counts collected earlier; the two counting paths can disagree on repeated words. |
| Line numbering | 1-based, lines deduplicated in `lines` field | One entry per *unique* line, not per occurrence. |
| Edge cases | `text==null` or `targetWordLength<0` → empty map; `targetWordLength==0` is accepted | Quietly skips empty tokens; null lines inside the text are not possible because `text.split` won't produce them. |
| Helper usage | `strlen`, `flipCase`, `howManyTimes` all called | But `flipCase`-as-normaliser is a no-op (double flip), and `howManyTimes` is called on a synthetic stream rather than on the original lines. |
| Extras | Inner `public static class WordInfo` with `toString`, getters, private `addOccurrence` | Inner class is not asked for and not forbidden; raises the surface area for integration tests. |

These observations are exactly the predicted Variant-1 weaknesses listed
in `prompt_design_log.md`: invented output type, ad-hoc tokenisation,
helpers called but not load-bearing, double-counting via two parallel
counting paths. They are *not* fixed in code; they are recorded so the
Step 11 table can show the unmodified-vs-edited gap honestly.

---

## Run 2 — Variant 1 → Gemini 3.1 Pro  *(complete)*

**Prompt sent.** Same prompt text as Run 1: "Variant 1 — Unmodified +
Combined prompt" from `bookscan/logs/prompt_design_log.md` at commit
`8a1982f`. Pasted verbatim into the Gemini 3.1 Pro web UI as a single
turn, no system-prompt additions.

**Model + access details.** Gemini 3.1 Pro via its hosted chat UI,
default sampling, run on 2026-05-24. Sent as a single turn; no follow-up
prompts.

**Response received.** Verbatim Java source below. The model returned
only the source (no surrounding prose). The paste preserved all
asterisks (no copy artefacts this time, in contrast to Run 1).

```java
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
```

**How the output was used.** Saved verbatim to
`bookscan/llm_b/unmodified/BookScan.java`, no edits. The transient
`bookscan/logs/response.txt` was deleted in the same commit.

**First observations (data for the Step 11 LLM-comparison table).**

| Decision | What Gemini 3.1 Pro chose | Notes |
|---|---|---|
| Public API name | `scanWordsOfLength(String text, int targetLength)` | Single-string text like Run 1; different method name from GPT-5.5's `scanWords`. |
| Output type | `Map<String, WordStats>` with inner `WordStats { word, totalOccurrences, lines }` | Both LLMs invented their own record type, with different field names. |
| Tokenisation | `split("[^a-zA-Z]+")` | ASCII-letter only — closer to the Variant 2 pinned rule than Run 1's Unicode-letter-or-digit rule, but still not identical and not asked for. |
| Case handling | `word.toLowerCase()` (built-in) for the key + `howManyTimes(line, word) + howManyTimes(line, flipCase(word))` | Uses `flipCase` to build the uppercase variant, but the comparison only checks the all-lowercase and all-uppercase forms of the line text — **MixedCase words like "The" or "TheCat" are missed entirely** (neither the lowercase substring "the" nor the uppercase "THE" appears in the literal text "The"). |
| `howManyTimes` integration | Direct substring search inside each raw line | No word-boundary delimiter, so e.g. searching for "cat" matches inside "concatenate" — **false positives on substring-of-longer-word**. |
| Line numbering | 1-based, lines deduplicated via `if (!lines.contains(n))` | Same de-dup pattern as GPT-5.5; one entry per *unique* line, not per occurrence. |
| Edge cases | `text==null` or `text.isEmpty()` → empty map; `targetLength<=0` not explicitly handled | Negative length falls through to the loop; would simply find no words of length −1. |
| Helper usage | `strlen` × 1, `flipCase` × 1 (key normalisation only), `howManyTimes` × 2 (per line per word) | All three helpers are called, unlike Run 1 where `flipCase` was used as the identity `flipCase(flipCase(x))`. |
| Extras | `WordStats` inner class with `getWord/getTotalOccurrences/getLines/toString` getters | Slightly richer than Run 1's `WordInfo`. |

**Run 1 vs Run 2 (both unmodified+combined):** different concrete bugs,
same underlying cause — the unmodified prompt did not pin the contract.

- Both invented an output type (`WordInfo` vs `WordStats`) and a method
  name (`scanWords` vs `scanWordsOfLength`).
- Both chose different tokenisation rules (`[^\p{L}\p{N}]+` vs
  `[^a-zA-Z]+`).
- GPT-5.5's case handling is a no-op (double `flipCase`); Gemini's case
  handling drops MixedCase words entirely.
- GPT-5.5 counts occurrences over a synthesised whitespace-separated
  stream; Gemini counts directly in the raw line text and therefore
  matches substrings of longer words ("cat" in "concatenate").

These are exactly the kinds of disagreements Variant 2 is intended to
eliminate; Runs 3–4 will measure how much.

---

## Run 3 — Variant 2 → GPT-5.5  *(pending)*

**Prompt sent.** See "Variant 2 — Edited + Combined prompt" section of
`bookscan/logs/prompt_design_log.md`.

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_a/edited/BookScan.java`._

---

## Run 4 — Variant 2 → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** Same as Run 3 (Variant 2 from `prompt_design_log.md`).

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/edited/BookScan.java`._
