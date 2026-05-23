"""Phase 2 / Step 4 orchestrator.

Compiles each of the four BookScan variants together with its SmokeMain,
runs the smoke driver, and writes the compile/run outcomes to
bookscan/reports/smoke_results.json. Intended to be re-run any time a
variant is regenerated.

Usage (from any directory):
    python bookscan/run_smoke.py
"""
from __future__ import annotations
import io, json, os, shutil, subprocess, sys, time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "bookscan"
REPORT = BOOK / "reports" / "smoke_results.json"

VARIANTS = [
    ("llm_a/unmodified", "GPT-5.5",        "unmodified+combined"),
    ("llm_b/unmodified", "Gemini 3.1 Pro", "unmodified+combined"),
    ("llm_a/edited",     "GPT-5.5",        "edited+combined"),
    ("llm_b/edited",     "Gemini 3.1 Pro", "edited+combined"),
]


def _run(cmd, cwd):
    try:
        proc = subprocess.run(
            cmd, cwd=cwd, capture_output=True, text=True,
            encoding="utf-8", errors="replace", timeout=60)
        return proc.returncode, proc.stdout, proc.stderr
    except subprocess.TimeoutExpired as e:
        return -1, e.stdout or "", "TIMEOUT after 60s"


def run_variant(rel_path: str) -> dict:
    variant_dir = BOOK / rel_path
    # Clean previous class files so a stale binary cannot mask a regression.
    for cls in variant_dir.glob("*.class"):
        cls.unlink()

    compile_rc, c_out, c_err = _run(
        ["javac", "-encoding", "UTF-8", "BookScan.java", "SmokeMain.java"],
        cwd=variant_dir,
    )
    compile_ok = compile_rc == 0 and (variant_dir / "BookScan.class").exists()

    run_rc, r_out, r_err = (None, "", "")
    run_ok = False
    if compile_ok:
        run_rc, r_out, r_err = _run(
            ["java", "-cp", ".", "SmokeMain"], cwd=variant_dir,
        )
        run_ok = run_rc == 0 and "SMOKE OK" in r_out

    return {
        "variant": rel_path,
        "compile": {
            "ok": compile_ok,
            "returncode": compile_rc,
            "stdout": c_out.strip(),
            "stderr": c_err.strip(),
        },
        "run": {
            "ok": run_ok,
            "returncode": run_rc,
            "stdout": r_out.strip(),
            "stderr": r_err.strip(),
        },
    }


def main() -> int:
    started = time.strftime("%Y-%m-%dT%H:%M:%S%z")
    results = []
    for rel_path, llm, prompt_variant in VARIANTS:
        print(f"[run_smoke] {rel_path} ({llm} / {prompt_variant}) ...",
              flush=True)
        r = run_variant(rel_path)
        r["llm"] = llm
        r["prompt_variant"] = prompt_variant
        results.append(r)
        status = "ok" if (r["compile"]["ok"] and r["run"]["ok"]) else "FAIL"
        print(f"[run_smoke]   compile={r['compile']['ok']}  "
              f"run={r['run']['ok']}  -> {status}",
              flush=True)

    summary = {
        "generated_at": started,
        "java_version": _run(["java", "-version"], cwd=str(BOOK))[2]
            .splitlines()[0] if shutil.which("java") else "missing",
        "variants_total": len(results),
        "compile_ok": sum(1 for r in results if r["compile"]["ok"]),
        "run_ok": sum(1 for r in results if r["run"]["ok"]),
        "results": results,
    }
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    with io.open(REPORT, "w", encoding="utf-8", newline="\n") as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)
    print(f"[run_smoke] wrote {REPORT.relative_to(ROOT)}")
    return 0 if summary["compile_ok"] == len(results) and \
                 summary["run_ok"] == len(results) else 1


if __name__ == "__main__":
    sys.exit(main())
