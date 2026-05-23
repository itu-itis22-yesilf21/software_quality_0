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

## Run 2 — Variant 1 → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** Same as Run 1 (Variant 1 from `prompt_design_log.md`).

**Model + access details.** _TBD: Gemini web UI, default sampling, date._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/unmodified/BookScan.java`._

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
