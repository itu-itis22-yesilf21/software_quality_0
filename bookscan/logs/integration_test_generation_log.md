# Phase 2 / Step 5 — Integration Test Generation Log

This log captures every LLM interaction that produced a
`BookScanIntegrationTest.java`. Format mirrors Phase 1's
`gemini_process/logs/code_generation_log.md` and Phase 2 / Step 3's
`class_generation_log.md`.

Each section records:

1. **Prompt sent** — the shared template from
   `bookscan/logs/integration_test_prompt_design.md` with this
   variant's `BookScan.java` source inlined under the
   `<INLINE THE FULL BookScan.java SOURCE HERE>` marker. We record the
   source byte length so the inlining is verifiable later.
2. **Model + access details** — model label, UI used, date, sampling.
3. **Response received** — verbatim, including any prose the model
   added outside the requested fenced code block.
4. **How the output was used** — saved path, any post-processing
   (e.g., paste-artefact restoration).

The four runs in this step:

| # | Source variant | LLM | Save path |
|---|---|---|---|
| 5.1 | `llm_a/unmodified/BookScan.java` | GPT-5.5         | `bookscan/llm_a/unmodified/BookScanIntegrationTest.java`  |
| 5.2 | `llm_b/unmodified/BookScan.java` | Gemini 3.1 Pro  | `bookscan/llm_b/unmodified/BookScanIntegrationTest.java`  |
| 5.3 | `llm_a/edited/BookScan.java`     | GPT-5.5         | `bookscan/llm_a/edited/BookScanIntegrationTest.java`      |
| 5.4 | `llm_b/edited/BookScan.java`     | Gemini 3.1 Pro  | `bookscan/llm_b/edited/BookScanIntegrationTest.java`      |

Each run gets its own step-tagged commit (`Phase 2 / Step 5.<run#>: ...`).

---

## Run 5.1 — `llm_a/unmodified/BookScan.java` → GPT-5.5  *(pending)*

**Prompt sent.** _TBD: template from
`bookscan/logs/integration_test_prompt_design.md` with
`bookscan/llm_a/unmodified/BookScan.java` inlined verbatim._

**Model + access details.** _TBD: GPT-5.5 web UI, default sampling,
date sent._

**Response received.** _TBD — paste full response here, including any
prose outside the code block._

**How the output was used.** _TBD — saved to
`bookscan/llm_a/unmodified/BookScanIntegrationTest.java`._

---

## Run 5.2 — `llm_b/unmodified/BookScan.java` → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/unmodified/BookScanIntegrationTest.java`._

---

## Run 5.3 — `llm_a/edited/BookScan.java` → GPT-5.5  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_a/edited/BookScanIntegrationTest.java`._

---

## Run 5.4 — `llm_b/edited/BookScan.java` → Gemini 3.1 Pro  *(pending)*

**Prompt sent.** _TBD: same template, this variant's source inlined._

**Model + access details.** _TBD._

**Response received.** _TBD._

**How the output was used.** _TBD — saved to
`bookscan/llm_b/edited/BookScanIntegrationTest.java`._
