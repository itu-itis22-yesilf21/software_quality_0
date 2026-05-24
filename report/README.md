# BLG 475E Phase 1 Report

## Files
- `report.tex` — IEEEtran journal-style report (Phase 1).
- `references.bib` — BibTeX entries for the 5 surveyed papers.

## How to compile

Local LaTeX (TeX Live / MiKTeX):

```
pdflatex report.tex
bibtex   report
pdflatex report.tex
pdflatex report.tex
```

Or upload both files to Overleaf:

1. Create a new project, choose **Upload Project** and add `report.tex` + `references.bib`.
2. Set the compiler to **pdfLaTeX**, the bibliography backend to **BibTeX**, and click **Recompile**.

The IEEEtran class is included in the standard TeX Live and Overleaf distributions; no extra installation is required.

## Things to fill in before submission
1. Replace `Author 1, Author 2, Author 3` on the title page with your real names.
2. Replace `LLM A` everywhere it appears with the actual name/version of the second LLM you used (e.g., GPT-4o, Claude Sonnet 4, etc.).
3. Replace the GitHub URL placeholder `<organisation-or-user>` in the conclusion with the real repository URL.
4. The literature review (Section II) was drafted from the surveyed papers but *the project rules say you cannot use LLMs for the literature review*. Rewrite Section II in your own words from the cited papers before you submit.
5. Distribute the per-step duties in the Acknowledgments section (the rules require you to specify the work split).
