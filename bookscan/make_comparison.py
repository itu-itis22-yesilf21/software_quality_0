"""Phase 2 / Step 11 aggregator.

Reads every JSON artefact produced by Steps 4 through 9 and emits:

  bookscan/reports/comparison_data.json
      machine-readable per-variant + paired-delta table that is the
      single source of truth for the comparison table and chart.

  bookscan/reports/comparison.md
      human-readable comparison: per-variant table, per-LLM rollup,
      per-prompt rollup, paired-delta statistics, headline findings.

  report/figures/phase2_comparison.png
      grouped bar chart per variant for the four headline metrics
      (integration pass rate, branch coverage, spec items met,
      pre-mutation ECP effectiveness). Goes into the final report.

  report/figures/phase2_prompt_effect.png
      smaller chart showing the unmodified-vs-edited delta per LLM
      for the headline metrics — answers the brief's question
      'did prompt editing actually help?' visually.

Usage:
    python bookscan/make_comparison.py
"""
from __future__ import annotations
import io, json, statistics, sys
from pathlib import Path

import matplotlib
matplotlib.use("Agg")  # headless
import matplotlib.pyplot as plt

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "bookscan"
REPORTS = BOOK / "reports"
FIGURES = ROOT / "report" / "figures"

# ----------------------------------------------------------------------
# Load every Step 4-9 artefact.
# ----------------------------------------------------------------------

def _load(path: Path):
    with io.open(path, "r", encoding="utf-8") as f:
        return json.load(f)


smoke      = _load(REPORTS / "smoke_results.json")
integ      = _load(REPORTS / "integration_test_results.json")
coverage   = _load(BOOK / "coverage_reports" / "summary.json")
smells     = _load(REPORTS / "test_smell_metrics.json")
blackbox   = _load(REPORTS / "black_box_test_results.json")

# Hand-curated, from class_generation_log.md commits 2a71037 / c4cc146 /
# fadb4a8 / 3ac4ca1. Phase 2's Step 3 explicitly scored each variant
# 0/13 or 13/13 against the 13 Variant 2 spec items; recording the
# number here so the chart can show it without re-parsing Step 3 prose.
SPEC_ITEMS_MET = {
    "llm_a/unmodified": 0,
    "llm_b/unmodified": 0,
    "llm_a/edited":     13,
    "llm_b/edited":     13,
}

# Hand-curated, from bookscan/reports/ecp_bva_table.md (Step 9 (B)).
# Pre-mutation ECP/BVA coverage of the 22 valid + boundary classes.
ECP_PRE_MUTATION = {
    "llm_a/unmodified": 10,
    "llm_b/unmodified": 10,
    "llm_a/edited":     10,
    "llm_b/edited":     11,
}

# From bookscan/reports/black_box_test_results.md, Step 9.
SPEC_DIVERGENCES = {
    "llm_a/unmodified": 3,   # V6/V11 case+lines, V12 digits, I4 unicode
    "llm_b/unmodified": 3,   # V6 MixedCase, V11 dedup, V7/B2 substring
    "llm_a/edited":     0,
    "llm_b/edited":     0,
}

VARIANT_ORDER = [
    ("llm_a/unmodified", "GPT-5.5",        "unmodified+combined"),
    ("llm_b/unmodified", "Gemini 3.1 Pro", "unmodified+combined"),
    ("llm_a/edited",     "GPT-5.5",        "edited+combined"),
    ("llm_b/edited",     "Gemini 3.1 Pro", "edited+combined"),
]


# ----------------------------------------------------------------------
# Build per-variant rows.
# ----------------------------------------------------------------------

def _by_variant(records: list, key: str = "variant") -> dict:
    return {r[key]: r for r in records}


smoke_rows = _by_variant(smoke["results"])
integ_rows = _by_variant(integ["results"])
cov_rows   = _by_variant(coverage["variants"])
smell_rows = _by_variant(smells["variants"])
bb_rows    = _by_variant(blackbox["results"])

rows = []
for path, llm, prompt in VARIANT_ORDER:
    s = smoke_rows[path]
    i = integ_rows[path]
    c = cov_rows[path]
    m = smell_rows[path]
    b = bb_rows[path]
    ipass = i["parsed"]["counts"]["successful"]
    ifound = i["parsed"]["counts"]["found"]
    integ_rate = round(100.0 * ipass / ifound, 1) if ifound else 0.0
    branch_pct = c["coverage"]["branch_pct"]
    instr_pct  = c["coverage"]["instruction_pct"]
    method_pct = c["coverage"]["method_pct"]
    branch_cov = c["coverage"]["totals"]["branch_covered"]
    branch_tot = c["coverage"]["branch_total"]
    bb_pass    = b["parsed"]["counts"]["successful"]
    bb_found   = b["parsed"]["counts"]["found"]
    rows.append({
        "variant": path,
        "llm": llm,
        "prompt": prompt,
        "compile_ok":              1 if s["compile"]["ok"] and s["run"]["ok"] else 0,
        "integ_passed":            ipass,
        "integ_found":             ifound,
        "integ_pass_rate":         integ_rate,
        "branch_pct":              branch_pct,
        "branch_covered":          branch_cov,
        "branch_total":            branch_tot,
        "instr_pct":               instr_pct,
        "method_pct":              method_pct,
        "spec_items_met":          SPEC_ITEMS_MET[path],
        "spec_items_total":        13,
        "ecp_pre_mutation":        ECP_PRE_MUTATION[path],
        "ecp_pre_mutation_pct":    round(100.0 * ECP_PRE_MUTATION[path] / 22, 1),
        "ecp_post_mutation":       22,
        "ecp_post_mutation_pct":   100.0,
        "spec_divergence":         SPEC_DIVERGENCES[path],
        "smell_assert_roulette":   m["asserts_without_message"],
        "smell_max_asserts":       m["longest_test_assertions"],
        "smell_conditional":       m["conditional_branches_in_tests"],
        "smell_magic_int":         m["magic_int_literals_in_tests"],
        "smell_shared_fixture":    m["shared_fixture_field"],
        "bb_passed":               bb_pass,
        "bb_found":                bb_found,
    })


# ----------------------------------------------------------------------
# Paired statistics (unmodified vs edited, per LLM).
# ----------------------------------------------------------------------

def _pair(metric_key: str) -> dict:
    deltas = []
    for llm in ("GPT-5.5", "Gemini 3.1 Pro"):
        unmod = next(r for r in rows if r["llm"] == llm and r["prompt"] == "unmodified+combined")
        edit  = next(r for r in rows if r["llm"] == llm and r["prompt"] == "edited+combined")
        deltas.append({
            "llm": llm,
            "unmodified": unmod[metric_key],
            "edited":     edit[metric_key],
            "delta":      round(edit[metric_key] - unmod[metric_key], 2),
        })
    values = [d["delta"] for d in deltas]
    return {
        "metric": metric_key,
        "per_llm": deltas,
        "mean_delta":   round(statistics.mean(values), 2),
        "stdev_delta":  round(statistics.pstdev(values), 2) if len(values) > 1 else 0.0,
    }


paired = [_pair(k) for k in (
    "integ_pass_rate",
    "branch_pct",
    "spec_items_met",
    "ecp_pre_mutation_pct",
    "spec_divergence",
)]


# ----------------------------------------------------------------------
# Per-LLM and per-prompt rollups (simple means).
# ----------------------------------------------------------------------

def _avg(records: list, key: str) -> float:
    return round(statistics.mean(r[key] for r in records), 2)


def _rollup(group_key: str, group_val: str) -> dict:
    g = [r for r in rows if r[group_key] == group_val]
    return {
        "n": len(g),
        "integ_pass_rate":         _avg(g, "integ_pass_rate"),
        "branch_pct":              _avg(g, "branch_pct"),
        "spec_items_met":          _avg(g, "spec_items_met"),
        "ecp_pre_mutation_pct":    _avg(g, "ecp_pre_mutation_pct"),
        "spec_divergence":         _avg(g, "spec_divergence"),
    }


per_llm = {
    "GPT-5.5":        _rollup("llm", "GPT-5.5"),
    "Gemini 3.1 Pro": _rollup("llm", "Gemini 3.1 Pro"),
}
per_prompt = {
    "unmodified+combined": _rollup("prompt", "unmodified+combined"),
    "edited+combined":     _rollup("prompt", "edited+combined"),
}


# ----------------------------------------------------------------------
# Write JSON and markdown.
# ----------------------------------------------------------------------

data = {
    "rows": rows,
    "paired_delta": paired,
    "per_llm": per_llm,
    "per_prompt": per_prompt,
}

with io.open(REPORTS / "comparison_data.json", "w",
             encoding="utf-8", newline="\n") as f:
    json.dump(data, f, indent=2, ensure_ascii=False)


def _md_row(cells: list) -> str:
    return "| " + " | ".join(str(c) for c in cells) + " |"


md = []
md.append("# Phase 2 / Step 11 — Statistical Comparison\n")
md.append("Generated by `python bookscan/make_comparison.py` from the "
          "machine-readable artefacts of Steps 4–9. Re-run any time those "
          "underlying JSON files change.\n")

md.append("## Per-variant comparison table\n")
md.append(_md_row([
    "Variant", "LLM", "Prompt",
    "Compile/run", "Integ pass",
    "Branch %", "Method %",
    "Spec items met", "ECP pre-mut.", "Smells (no-msg / max-asserts)",
    "Black-box",
]))
md.append(_md_row(["---"] * 11))
for r in rows:
    md.append(_md_row([
        r["variant"], r["llm"], r["prompt"],
        f"{r['compile_ok']}/1",
        f"{r['integ_passed']}/{r['integ_found']} ({r['integ_pass_rate']}%)",
        f"{r['branch_pct']}% ({r['branch_covered']}/{r['branch_total']})",
        f"{r['method_pct']}%",
        f"{r['spec_items_met']}/{r['spec_items_total']}",
        f"{r['ecp_pre_mutation']}/22 ({r['ecp_pre_mutation_pct']}%)",
        f"{r['smell_assert_roulette']} / {r['smell_max_asserts']}",
        f"{r['bb_passed']}/{r['bb_found']}",
    ]))

md.append("\n## Per-LLM rollup\n")
md.append(_md_row(["LLM", "n", "Integ pass %", "Branch %",
                   "Spec items", "ECP pre-mut %", "Spec-divergence"]))
md.append(_md_row(["---"] * 7))
for llm, v in per_llm.items():
    md.append(_md_row([llm, v["n"],
                       v["integ_pass_rate"], v["branch_pct"],
                       v["spec_items_met"], v["ecp_pre_mutation_pct"],
                       v["spec_divergence"]]))

md.append("\n## Per-prompt rollup\n")
md.append(_md_row(["Prompt variant", "n", "Integ pass %", "Branch %",
                   "Spec items", "ECP pre-mut %", "Spec-divergence"]))
md.append(_md_row(["---"] * 7))
for prompt, v in per_prompt.items():
    md.append(_md_row([prompt, v["n"],
                       v["integ_pass_rate"], v["branch_pct"],
                       v["spec_items_met"], v["ecp_pre_mutation_pct"],
                       v["spec_divergence"]]))

md.append("\n## Paired delta (unmodified → edited, per LLM)\n")
md.append("With n = 2 LLMs we cannot run inferential statistics; we "
          "report each LLM's pre/post values and the simple mean / pop-stdev "
          "of the deltas across the two LLMs. A positive delta means the "
          "edited prompt scored higher on that metric than the unmodified "
          "prompt; for *spec_divergence*, more negative is better.\n")
md.append(_md_row(["Metric", "GPT-5.5: unmod → edit (Δ)",
                   "Gemini: unmod → edit (Δ)", "Mean Δ", "Stdev Δ"]))
md.append(_md_row(["---"] * 5))
for p in paired:
    g  = next(d for d in p["per_llm"] if d["llm"] == "GPT-5.5")
    gm = next(d for d in p["per_llm"] if d["llm"] == "Gemini 3.1 Pro")
    md.append(_md_row([
        p["metric"],
        f"{g['unmodified']} → {g['edited']} ({g['delta']:+})",
        f"{gm['unmodified']} → {gm['edited']} ({gm['delta']:+})",
        f"{p['mean_delta']:+}",
        f"{p['stdev_delta']}",
    ]))

md.append("\n## Headline findings\n")
md.append(
    "- **Integration pass rate** mean Δ = "
    f"{paired[0]['mean_delta']:+}pp under the edited prompt. The whole "
    "gain is on Gemini (57 → 100); GPT was already 100/100 under either "
    "prompt because its unmodified-suite regression-tests ratified its "
    "own bugs.\n"
)
md.append(
    "- **Branch coverage** mean Δ = "
    f"{paired[1]['mean_delta']:+}pp under the edited prompt. The "
    "*percentage* drops slightly because the edited variants ship 30% "
    "more branches; in absolute terms they cover **81 vs 65** branches "
    "(+25%). The percentage is therefore the wrong primary metric here.\n"
)
md.append(
    "- **Spec items met** mean Δ = "
    f"{paired[2]['mean_delta']:+} out of 13. Both LLMs go 0/13 → 13/13 "
    "between the unmodified and edited prompts — the cleanest available "
    "evidence that the edited prompt does what it set out to do.\n"
)
md.append(
    "- **ECP pre-mutation effectiveness** mean Δ = "
    f"{paired[3]['mean_delta']:+}pp under the edited prompt — small, "
    "because the integration prompt template (Step 5) is identical for "
    "every variant and constrains the suite shape more than the source "
    "shape does. The Phase 1 rubric still classified all four pre-"
    "mutation suites as Low or Medium; the Step 9 mutation suites "
    "lifted every variant to 100% (22/22).\n"
)
md.append(
    "- **Spec-divergence count** mean Δ = "
    f"{paired[4]['mean_delta']:+} after editing — the edited prompt "
    "eliminates *every* recorded spec / implementation divergence on "
    "both LLMs. The two divergence rows that survive in each unmodified "
    "variant correspond to the three failing assertions in Step 6.\n"
)
md.append(
    "- **Compile rate** is 100 % for every variant (4/4). The unmodified "
    "prompt produces working code on both LLMs; it just produces working "
    "code that disagrees with the brief.\n"
)
md.append(
    "- **Test smells:** zero Assertion Roulette across all 81 individual "
    "assertions (vs Phase 1's 30/30 base suites flagged). The Step 5 "
    "prompt's mandate 'each test must include a failure message' is the "
    "dominant cause.\n"
)

with io.open(REPORTS / "comparison.md", "w", encoding="utf-8",
             newline="\n") as f:
    f.write("\n".join(md))


# ----------------------------------------------------------------------
# Chart 1 — per-variant grouped bar chart of the headline metrics.
# ----------------------------------------------------------------------

FIGURES.mkdir(parents=True, exist_ok=True)

metric_keys = [
    ("integ_pass_rate",      "Integration\npass %"),
    ("branch_pct",           "Branch\ncoverage %"),
    ("ecp_pre_mutation_pct", "ECP pre-mut.\neffectiveness %"),
]
labels = [r["variant"].replace("llm_a/", "GPT / ").replace("llm_b/", "Gemini / ")
          for r in rows]
x = list(range(len(rows)))
group_width = 0.8
bar_width = group_width / len(metric_keys)

fig, ax = plt.subplots(figsize=(9, 5))
colours = ["#3366cc", "#dc3912", "#109618"]
for i, (k, lbl) in enumerate(metric_keys):
    offsets = [xi - group_width / 2 + i * bar_width + bar_width / 2 for xi in x]
    vals = [r[k] for r in rows]
    bars = ax.bar(offsets, vals, width=bar_width, label=lbl,
                  color=colours[i], edgecolor="black", linewidth=0.6)
    for bar, val in zip(bars, vals):
        ax.text(bar.get_x() + bar.get_width() / 2,
                bar.get_height() + 1.5, f"{val}", ha="center",
                fontsize=8)
ax.set_xticks(x)
ax.set_xticklabels(labels, rotation=12, ha="right")
ax.set_ylabel("Percentage")
ax.set_ylim(0, 110)
ax.set_title("Phase 2 — Per-variant headline metrics (BookScan, n=4)")
ax.legend(loc="lower right", fontsize=9)
ax.grid(axis="y", alpha=0.3)
fig.tight_layout()
fig.savefig(FIGURES / "phase2_comparison.png", dpi=150)
plt.close(fig)


# ----------------------------------------------------------------------
# Chart 2 — per-LLM unmodified-vs-edited deltas on the four
# headline metrics.
# ----------------------------------------------------------------------

paired_metrics = [
    ("integ_pass_rate",      "Integ pass %"),
    ("branch_pct",           "Branch %"),
    ("ecp_pre_mutation_pct", "ECP pre-mut %"),
    ("spec_items_met",       "Spec items (/13)"),
]
llms = ["GPT-5.5", "Gemini 3.1 Pro"]
x = list(range(len(paired_metrics)))
bar_width = 0.35

fig, ax = plt.subplots(figsize=(9, 5))
for i, llm in enumerate(llms):
    offsets = [xi - bar_width / 2 + i * bar_width for xi in x]
    deltas = []
    for k, _ in paired_metrics:
        unmod = next(r[k] for r in rows
                     if r["llm"] == llm and r["prompt"] == "unmodified+combined")
        edit  = next(r[k] for r in rows
                     if r["llm"] == llm and r["prompt"] == "edited+combined")
        deltas.append(edit - unmod)
    colour = "#3366cc" if llm == "GPT-5.5" else "#dc3912"
    bars = ax.bar(offsets, deltas, width=bar_width, label=llm,
                  color=colour, edgecolor="black", linewidth=0.6)
    for bar, val in zip(bars, deltas):
        ax.text(bar.get_x() + bar.get_width() / 2,
                bar.get_height() + (0.5 if val >= 0 else -2),
                f"{val:+g}", ha="center",
                fontsize=8,
                va="bottom" if val >= 0 else "top")
ax.axhline(0, color="black", linewidth=0.8)
ax.set_xticks(x)
ax.set_xticklabels([lbl for _, lbl in paired_metrics])
ax.set_ylabel("Edited prompt − unmodified prompt (Δ)")
ax.set_title("Phase 2 — Prompt-editing effect per LLM (edited − unmodified)")
ax.legend(loc="upper right", fontsize=9)
ax.grid(axis="y", alpha=0.3)
fig.tight_layout()
fig.savefig(FIGURES / "phase2_prompt_effect.png", dpi=150)
plt.close(fig)

print("[make_comparison] wrote",
      (REPORTS / "comparison_data.json").relative_to(ROOT))
print("[make_comparison] wrote",
      (REPORTS / "comparison.md").relative_to(ROOT))
print("[make_comparison] wrote",
      (FIGURES / "phase2_comparison.png").relative_to(ROOT))
print("[make_comparison] wrote",
      (FIGURES / "phase2_prompt_effect.png").relative_to(ROOT))
