"""Phase 2 / Step 9 orchestrator.

Compiles each BookScan + BookScanBlackBoxTest pair and runs the
black-box mutation suite, choosing the runner per variant (same rule
as bookscan/run_integration.py):

- main-driver suites (GPT) → `java BookScanBlackBoxTest`
- JUnit 5 suites (Gemini) → JUnit Platform Console Standalone launcher

Results are written to bookscan/reports/black_box_test_results.json
and a human summary at bookscan/reports/black_box_test_results.md.

Usage:
    python bookscan/run_black_box.py
"""
from __future__ import annotations
import io, json, os, re, subprocess, sys, time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "bookscan"
JUNIT_JAR = ROOT / "gemini_process" / "junit-platform-console-standalone-1.9.3.jar"
REPORT_JSON = BOOK / "reports" / "black_box_test_results.json"

VARIANTS = [
    ("llm_a/unmodified", "GPT-5.5",        "unmodified+combined", "main"),
    ("llm_b/unmodified", "Gemini 3.1 Pro", "unmodified+combined", "junit5"),
    ("llm_a/edited",     "GPT-5.5",        "edited+combined",     "main"),
    ("llm_b/edited",     "Gemini 3.1 Pro", "edited+combined",     "junit5"),
]


def _run(cmd, cwd):
    try:
        proc = subprocess.run(
            cmd, cwd=cwd, capture_output=True, text=True,
            encoding="utf-8", errors="replace", timeout=120,
        )
        return proc.returncode, proc.stdout, proc.stderr
    except subprocess.TimeoutExpired as e:
        return -1, e.stdout or "", "TIMEOUT after 120s"


def _parse_junit_summary(stdout: str) -> dict:
    counts = {"found": 0, "skipped": 0, "successful": 0, "failed": 0}
    for key in counts:
        m = re.search(rf"\[\s*(\d+)\s+tests {key}\s*\]", stdout)
        if m:
            counts[key] = int(m.group(1))
    failures = []
    in_failures = False
    for line in stdout.splitlines():
        if line.strip().startswith("Failures (") and line.strip().endswith(":"):
            in_failures = True
            continue
        if in_failures:
            stripped = line.strip()
            if not stripped:
                continue
            if (stripped.startswith("Test run finished")
                or (stripped.startswith("[") and "tests" in stripped)):
                in_failures = False
                continue
            m = re.search(r"BookScanBlackBoxTest:([A-Za-z0-9_]+)\(\)", stripped)
            if m:
                failures.append(m.group(1))
    return {"counts": counts, "failures": failures}


def _parse_main_summary(stdout: str, stderr: str, rc: int) -> dict:
    """For main-driver black-box suites we don't have a built-in test
    count, but the file is structured so every `check(condition, msg)`
    contributes one logical assertion. We approximate by counting the
    "All BookScanBlackBoxTest assertions passed." sentinel for full
    pass, and a single first-failure on AssertionError otherwise.
    The companion markdown gives the per-class breakdown."""
    if rc == 0 and "All BookScanBlackBoxTest assertions passed." in stdout:
        return {"counts": {"found": 1, "skipped": 0,
                           "successful": 1, "failed": 0},
                "failures": []}
    return {"counts": {"found": 1, "skipped": 0,
                       "successful": 0, "failed": 1},
            "failures": [stderr.strip().splitlines()[0]
                         if stderr.strip() else "AssertionError"]}


def run_variant(rel_path: str, framework: str) -> dict:
    variant_dir = BOOK / rel_path
    for cls in variant_dir.glob("*.class"):
        cls.unlink()

    if framework == "main":
        compile_cmd = ["javac", "-encoding", "UTF-8",
                       "BookScan.java", "BookScanBlackBoxTest.java"]
    else:
        compile_cmd = ["javac", "-encoding", "UTF-8",
                       "-cp", f".;{JUNIT_JAR}",
                       "BookScan.java", "BookScanBlackBoxTest.java"]
    crc, c_out, c_err = _run(compile_cmd, cwd=variant_dir)
    compile_ok = crc == 0 and (variant_dir / "BookScanBlackBoxTest.class").exists()

    run_rc, r_out, r_err, parsed = None, "", "", None
    if compile_ok:
        if framework == "main":
            cmd = ["java", "-cp", ".", "BookScanBlackBoxTest"]
            run_rc, r_out, r_err = _run(cmd, cwd=variant_dir)
            parsed = _parse_main_summary(r_out, r_err, run_rc)
        else:
            cmd = ["java", "-jar", str(JUNIT_JAR),
                   "--class-path", ".",
                   "--select-class", "BookScanBlackBoxTest",
                   "--disable-banner", "--disable-ansi-colors"]
            run_rc, r_out, r_err = _run(cmd, cwd=variant_dir)
            parsed = _parse_junit_summary(r_out)

    return {
        "variant": rel_path,
        "framework": framework,
        "compile": {
            "ok": compile_ok,
            "returncode": crc,
            "stderr": c_err.strip(),
        },
        "run": {
            "returncode": run_rc,
            "stdout": r_out.strip(),
            "stderr": r_err.strip(),
        },
        "parsed": parsed,
    }


def main() -> int:
    started = time.strftime("%Y-%m-%dT%H:%M:%S%z")
    results = []
    total_found = total_pass = total_fail = 0
    for rel_path, llm, prompt_variant, framework in VARIANTS:
        print(f"[run_black_box] {rel_path} ({llm} / {prompt_variant}, "
              f"framework={framework}) ...", flush=True)
        r = run_variant(rel_path, framework)
        r["llm"] = llm
        r["prompt_variant"] = prompt_variant
        results.append(r)
        if r["compile"]["ok"] and r["parsed"]:
            c = r["parsed"]["counts"]
            total_found += c["found"]
            total_pass += c["successful"]
            total_fail += c["failed"]
            print(f"[run_black_box]   compile=ok  found={c['found']}  "
                  f"pass={c['successful']}  fail={c['failed']}  "
                  f"failures={r['parsed']['failures']}",
                  flush=True)
        else:
            print(f"[run_black_box]   COMPILE FAIL  "
                  f"rc={r['compile']['returncode']}  err={r['compile']['stderr'][:200]}",
                  flush=True)

    summary = {
        "generated_at": started,
        "variants_total": len(results),
        "totals": {
            "tests_found":  total_found,
            "tests_passed": total_pass,
            "tests_failed": total_fail,
        },
        "results": results,
    }
    REPORT_JSON.parent.mkdir(parents=True, exist_ok=True)
    with io.open(REPORT_JSON, "w", encoding="utf-8", newline="\n") as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)
    print(f"[run_black_box] wrote {REPORT_JSON.relative_to(ROOT)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
