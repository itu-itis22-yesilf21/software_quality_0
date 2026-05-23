"""Phase 2 / Step 6 orchestrator.

Compiles each BookScan + BookScanIntegrationTest pair and runs the
integration suite, choosing the right runner per variant:

- Variants where the model picked a `public static void main` driver
  (GPT-5.5 in Runs 5.1 and 5.3) run via `java BookScanIntegrationTest`.
- Variants that emitted JUnit 5 `@Test` methods (Gemini 3.1 Pro in Runs
  5.2 and 5.4) run via the JUnit Platform Console Standalone launcher
  already shipped with Phase 1 (gemini_process/junit-platform-console-
  standalone-1.9.3.jar).

Results are written to bookscan/reports/integration_test_results.json
(machine) and a human summary at integration_test_results.md.

Usage:
    python bookscan/run_integration.py
"""
from __future__ import annotations
import io, json, os, re, shutil, subprocess, sys, time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "bookscan"
JUNIT_JAR = ROOT / "gemini_process" / "junit-platform-console-standalone-1.9.3.jar"
REPORT_JSON = BOOK / "reports" / "integration_test_results.json"
REPORT_MD = BOOK / "reports" / "integration_test_results.md"

# Each entry declares (rel_path, llm, prompt_variant, framework).
# framework is "main" for main-driver suites or "junit5" for JUnit 5.
VARIANTS = [
    ("llm_a/unmodified", "GPT-5.5",        "unmodified+combined", "main"),
    ("llm_b/unmodified", "Gemini 3.1 Pro", "unmodified+combined", "junit5"),
    ("llm_a/edited",     "GPT-5.5",        "edited+combined",     "main"),
    ("llm_b/edited",     "Gemini 3.1 Pro", "edited+combined",     "junit5"),
]


def _run(cmd, cwd, env=None):
    try:
        proc = subprocess.run(
            cmd, cwd=cwd, capture_output=True, text=True,
            encoding="utf-8", errors="replace",
            timeout=120, env=env,
        )
        return proc.returncode, proc.stdout, proc.stderr
    except subprocess.TimeoutExpired as e:
        return -1, e.stdout or "", "TIMEOUT after 120s"


def _parse_junit_summary(stdout: str) -> dict:
    """Parse the 'Test execution started.' summary block emitted by
    junit-platform-console-standalone.

    The console-launcher writes a structured summary like:
        [        10 tests found           ]
        [         0 tests skipped         ]
        [         7 tests successful      ]
        [         3 tests failed          ]
    plus a `Failures` section with each failing test's name.
    """
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
            if stripped.startswith("Test run finished") or stripped.startswith("[") and "tests" in stripped:
                in_failures = False
                continue
            # JUnit prints "JUnit Jupiter:BookScanIntegrationTest:methodName()"
            # under Failures (...). Capture only the method name to keep the
            # report concise.
            m = re.search(r"BookScanIntegrationTest:([A-Za-z0-9_]+)\(\)", stripped)
            if m:
                failures.append(m.group(1))
    return {"counts": counts, "failures": failures}


def _parse_main_summary(stdout: str, stderr: str, rc: int) -> dict:
    """Main-driver suites print 'All BookScan integration tests passed.'
    when every requirement passed and throw AssertionError otherwise.

    We don't have per-test counts the way JUnit does, but we know the
    Step-5 prompt asked for seven requirements. Treat the suite as
    7 tests, all passing iff `rc==0` and the "All ... passed" sentinel
    appears, otherwise one failure whose name comes from the
    AssertionError message.
    """
    counts = {"found": 7, "skipped": 0, "successful": 0, "failed": 0}
    failures = []
    if rc == 0 and "All BookScan integration tests passed." in stdout:
        counts["successful"] = 7
    else:
        # AssertionError messages always begin with "Requirement N failed:".
        msg = stderr or stdout
        m = re.search(r"Requirement\s+(\d+)\s+failed", msg)
        first_failed = int(m.group(1)) if m else None
        if first_failed is not None:
            counts["failed"] = 1
            counts["successful"] = first_failed - 1   # earlier requirements ran
            failures.append(f"requirement{first_failed}")
        else:
            counts["failed"] = 7
            counts["successful"] = 0
            failures.append("uncategorised-failure")
    return {"counts": counts, "failures": failures}


def run_variant(rel_path: str, framework: str) -> dict:
    variant_dir = BOOK / rel_path
    for cls in variant_dir.glob("*.class"):
        cls.unlink()

    # 1. Compile.
    if framework == "main":
        compile_cmd = ["javac", "-encoding", "UTF-8",
                       "BookScan.java", "BookScanIntegrationTest.java"]
    else:
        compile_cmd = ["javac", "-encoding", "UTF-8",
                       "-cp", f".;{JUNIT_JAR}",
                       "BookScan.java", "BookScanIntegrationTest.java"]
    compile_rc, c_out, c_err = _run(compile_cmd, cwd=variant_dir)
    compile_ok = compile_rc == 0 and (variant_dir / "BookScanIntegrationTest.class").exists()

    # 2. Run.
    run_rc, r_out, r_err, parsed = None, "", "", None
    if compile_ok:
        if framework == "main":
            run_cmd = ["java", "-cp", ".", "BookScanIntegrationTest"]
            run_rc, r_out, r_err = _run(run_cmd, cwd=variant_dir)
            parsed = _parse_main_summary(r_out, r_err, run_rc)
        else:
            run_cmd = ["java", "-jar", str(JUNIT_JAR),
                       "--class-path", ".",
                       "--select-class", "BookScanIntegrationTest",
                       "--disable-banner", "--disable-ansi-colors"]
            run_rc, r_out, r_err = _run(run_cmd, cwd=variant_dir)
            parsed = _parse_junit_summary(r_out)

    return {
        "variant": rel_path,
        "framework": framework,
        "compile": {
            "ok": compile_ok,
            "returncode": compile_rc,
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
        print(f"[run_integration] {rel_path} ({llm} / {prompt_variant}, "
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
            print(f"[run_integration]   compile=ok  "
                  f"found={c['found']}  pass={c['successful']}  "
                  f"fail={c['failed']}  failures={r['parsed']['failures']}",
                  flush=True)
        else:
            print(f"[run_integration]   COMPILE FAIL  rc={r['compile']['returncode']}",
                  flush=True)

    summary = {
        "generated_at": started,
        "variants_total": len(results),
        "totals": {
            "tests_found": total_found,
            "tests_passed": total_pass,
            "tests_failed": total_fail,
        },
        "results": results,
    }
    REPORT_JSON.parent.mkdir(parents=True, exist_ok=True)
    with io.open(REPORT_JSON, "w", encoding="utf-8", newline="\n") as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)
    print(f"[run_integration] wrote {REPORT_JSON.relative_to(ROOT)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
