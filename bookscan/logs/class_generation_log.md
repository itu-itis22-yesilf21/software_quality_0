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

## Run 1 — Variant 1 → GPT-5.5  *(pending)*

**Prompt sent.** See "Variant 1 — Unmodified + Combined prompt" section of
`bookscan/logs/prompt_design_log.md`. Will be pasted verbatim here once
the response comes back.

**Model + access details.** _TBD: GPT-5.5 web UI, default sampling,
date sent._

**Response received.** _TBD — paste full response here, including any
prose outside the code block._

**How the output was used.** _TBD — saved to
`bookscan/llm_a/unmodified/BookScan.java`. Note any post-processing._

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
