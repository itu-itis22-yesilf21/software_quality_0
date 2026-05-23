"""Phase 2 / Step 7 orchestrator.

For each of the four BookScan variants:
  1. Compile BookScan + BookScanIntegrationTest (idempotent; reuses
     the Step 6 build path).
  2. Run the integration suite with the JaCoCo agent attached,
     instrumenting only BookScan* classes. Writes coverage.exec into
     bookscan/coverage_reports/<variant>/.
  3. Generate per-variant CSV and XML reports via jacococli report.
  4. Parse the CSV to extract per-class branch and instruction
     coverage counts, sum them per variant, and aggregate everything
     into bookscan/coverage_reports/summary.json plus a human-readable
     markdown summary.

Mirrors the Phase 1 workflow used in coverage_reports/ (same JaCoCo
0.8.12 jars).

Usage:
    python bookscan/run_coverage.py
"""
from __future__ import annotations
import csv, io, json, os, re, shutil, subprocess, sys, time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "bookscan"
COVERAGE = BOOK / "coverage_reports"
TOOLS = ROOT / "tools"
JACOCO_AGENT = TOOLS / "jacocoagent.jar"
JACOCO_CLI = TOOLS / "jacococli.jar"
JUNIT_JAR = ROOT / "gemini_process" / "junit-platform-console-standalone-1.9.3.jar"

SUMMARY_JSON = COVERAGE / "summary.json"
SUMMARY_MD = COVERAGE / "summary.md"

VARIANTS = [
    ("llm_a/unmodified", "GPT-5.5",        "unmodified+combined", "main"),
    ("llm_b/unmodified", "Gemini 3.1 Pro", "unmodified+combined", "junit5"),
    ("llm_a/edited",     "GPT-5.5",        "edited+combined",     "main"),
    ("llm_b/edited",     "Gemini 3.1 Pro", "edited+combined",     "junit5"),
]


def _run(cmd, cwd=None):
    try:
        proc = subprocess.run(
            cmd, cwd=cwd, capture_output=True, text=True,
            encoding="utf-8", errors="replace", timeout=120,
        )
        return proc.returncode, proc.stdout, proc.stderr
    except subprocess.TimeoutExpired as e:
        return -1, e.stdout or "", "TIMEOUT after 120s"


def compile_variant(variant_dir: Path, framework: str) -> bool:
    for cls in variant_dir.glob("*.class"):
        cls.unlink()
    if framework == "main":
        cmd = ["javac", "-encoding", "UTF-8",
               "BookScan.java", "BookScanIntegrationTest.java"]
    else:
        cmd = ["javac", "-encoding", "UTF-8",
               "-cp", f".;{JUNIT_JAR}",
               "BookScan.java", "BookScanIntegrationTest.java"]
    rc, _, err = _run(cmd, cwd=variant_dir)
    ok = rc == 0 and (variant_dir / "BookScanIntegrationTest.class").exists()
    if not ok:
        print(f"  COMPILE FAIL: {err}")
    return ok


def run_with_agent(variant_dir: Path, framework: str, exec_dest: Path) -> tuple[int, str, str]:
    exec_dest.parent.mkdir(parents=True, exist_ok=True)
    if exec_dest.exists():
        exec_dest.unlink()
    agent_opt = (f"-javaagent:{JACOCO_AGENT}="
                 f"destfile={exec_dest},includes=BookScan*,"
                 f"output=file,append=false")
    if framework == "main":
        cmd = ["java", agent_opt, "-cp", ".", "BookScanIntegrationTest"]
    else:
        cmd = ["java", agent_opt, "-jar", str(JUNIT_JAR),
               "--class-path", ".",
               "--select-class", "BookScanIntegrationTest",
               "--disable-banner", "--disable-ansi-colors"]
    return _run(cmd, cwd=variant_dir)


def generate_report(variant_dir: Path, exec_file: Path, report_dir: Path) -> tuple[Path, Path]:
    report_dir.mkdir(parents=True, exist_ok=True)
    csv_path = report_dir / "BookScan.csv"
    xml_path = report_dir / "BookScan.xml"
    # JaCoCo CLI report takes classfiles via --classfiles. Pass every
    # BookScan*.class so inner classes (WordInfo, WordStats) are included.
    class_args = []
    for cls in sorted(variant_dir.glob("BookScan*.class")):
        # exclude the test class itself from the report; only BookScan + its
        # inner classes count toward production-code coverage.
        if cls.name == "BookScanIntegrationTest.class":
            continue
        class_args += ["--classfiles", str(cls)]
    cmd = ["java", "-jar", str(JACOCO_CLI), "report", str(exec_file),
           *class_args,
           "--sourcefiles", str(variant_dir),
           "--csv", str(csv_path),
           "--xml", str(xml_path),
           "--name", f"BookScan ({variant_dir.relative_to(BOOK)})"]
    rc, out, err = _run(cmd)
    if rc != 0:
        print(f"  REPORT FAIL: rc={rc} out={out} err={err}")
    return csv_path, xml_path


def parse_csv(csv_path: Path) -> dict:
    """Sum branch / instruction / line / method counters across rows.

    JaCoCo CSV columns: GROUP, PACKAGE, CLASS, INSTRUCTION_MISSED,
    INSTRUCTION_COVERED, BRANCH_MISSED, BRANCH_COVERED, LINE_MISSED,
    LINE_COVERED, COMPLEXITY_MISSED, COMPLEXITY_COVERED, METHOD_MISSED,
    METHOD_COVERED.
    """
    totals = {
        "instruction_missed": 0, "instruction_covered": 0,
        "branch_missed": 0,      "branch_covered": 0,
        "line_missed": 0,        "line_covered": 0,
        "method_missed": 0,      "method_covered": 0,
    }
    per_class = []
    if not csv_path.exists():
        return {"totals": totals, "per_class": per_class}
    with io.open(csv_path, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            try:
                im, ic = int(row["INSTRUCTION_MISSED"]), int(row["INSTRUCTION_COVERED"])
                bm, bc = int(row["BRANCH_MISSED"]),      int(row["BRANCH_COVERED"])
                lm, lc = int(row["LINE_MISSED"]),        int(row["LINE_COVERED"])
                mm, mc = int(row["METHOD_MISSED"]),      int(row["METHOD_COVERED"])
            except (KeyError, ValueError):
                continue
            totals["instruction_missed"] += im
            totals["instruction_covered"] += ic
            totals["branch_missed"]      += bm
            totals["branch_covered"]     += bc
            totals["line_missed"]        += lm
            totals["line_covered"]       += lc
            totals["method_missed"]      += mm
            totals["method_covered"]     += mc
            per_class.append({
                "class": row.get("CLASS", "?"),
                "branch_missed": bm,
                "branch_covered": bc,
                "branch_total": bm + bc,
                "branch_pct": _pct(bc, bm + bc),
                "instruction_pct": _pct(ic, im + ic),
                "line_pct": _pct(lc, lm + lc),
                "method_pct": _pct(mc, mm + mc),
            })
    bt = totals["branch_missed"] + totals["branch_covered"]
    it = totals["instruction_missed"] + totals["instruction_covered"]
    lt = totals["line_missed"] + totals["line_covered"]
    mt = totals["method_missed"] + totals["method_covered"]
    return {
        "totals": totals,
        "branch_total": bt,
        "instruction_total": it,
        "line_total": lt,
        "method_total": mt,
        "branch_pct": _pct(totals["branch_covered"], bt),
        "instruction_pct": _pct(totals["instruction_covered"], it),
        "line_pct": _pct(totals["line_covered"], lt),
        "method_pct": _pct(totals["method_covered"], mt),
        "per_class": per_class,
    }


def _pct(covered: int, total: int) -> float:
    return round(100.0 * covered / total, 2) if total else 100.0


def main() -> int:
    started = time.strftime("%Y-%m-%dT%H:%M:%S%z")
    results = []
    for rel_path, llm, prompt_variant, framework in VARIANTS:
        print(f"[run_coverage] {rel_path} ({llm} / {prompt_variant}) ...",
              flush=True)
        variant_dir = BOOK / rel_path
        report_dir = COVERAGE / rel_path.replace("/", "_")
        report_dir.mkdir(parents=True, exist_ok=True)
        exec_file = report_dir / "coverage.exec"

        if not compile_variant(variant_dir, framework):
            results.append({"variant": rel_path, "compile_ok": False})
            continue
        rc, out, err = run_with_agent(variant_dir, framework, exec_file)
        run_ok = exec_file.exists() and exec_file.stat().st_size > 0
        # Note: even when the test suite has failing assertions (Run 5.2),
        # the agent still emits the .exec file as long as the JVM exited
        # cleanly enough to run the shutdown hook. Non-zero rc is normal
        # here for the junit5 variant with failing tests.
        csv_path, xml_path = generate_report(variant_dir, exec_file, report_dir)
        parsed = parse_csv(csv_path)
        results.append({
            "variant": rel_path,
            "llm": llm,
            "prompt_variant": prompt_variant,
            "framework": framework,
            "compile_ok": True,
            "run_returncode": rc,
            "run_ok": run_ok,
            "exec_path": str(exec_file.relative_to(ROOT)),
            "csv_path": str(csv_path.relative_to(ROOT)),
            "xml_path": str(xml_path.relative_to(ROOT)),
            "coverage": parsed,
        })
        print(f"  branch: {parsed['branch_pct']}%  "
              f"({parsed['totals']['branch_covered']}/{parsed['branch_total']})  "
              f"instruction: {parsed['instruction_pct']}%  "
              f"line: {parsed['line_pct']}%  method: {parsed['method_pct']}%",
              flush=True)

    summary = {
        "generated_at": started,
        "jacoco_version": "0.8.12",
        "variants": results,
        "aggregate": _aggregate(results),
    }
    SUMMARY_JSON.parent.mkdir(parents=True, exist_ok=True)
    with io.open(SUMMARY_JSON, "w", encoding="utf-8", newline="\n") as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)
    print(f"[run_coverage] wrote {SUMMARY_JSON.relative_to(ROOT)}")
    return 0


def _aggregate(results: list) -> dict:
    valid = [r for r in results if r.get("coverage")]
    if not valid:
        return {}
    bcov = sum(r["coverage"]["totals"]["branch_covered"] for r in valid)
    btot = sum(r["coverage"]["branch_total"] for r in valid)
    icov = sum(r["coverage"]["totals"]["instruction_covered"] for r in valid)
    itot = sum(r["coverage"]["instruction_total"] for r in valid)
    return {
        "total_variants": len(results),
        "covered_variants": len(valid),
        "mean_branch_pct": round(
            sum(r["coverage"]["branch_pct"] for r in valid) / len(valid), 2),
        "mean_instruction_pct": round(
            sum(r["coverage"]["instruction_pct"] for r in valid) / len(valid), 2),
        "weighted_branch_pct": _pct(bcov, btot),
        "weighted_instruction_pct": _pct(icov, itot),
    }


if __name__ == "__main__":
    sys.exit(main())
