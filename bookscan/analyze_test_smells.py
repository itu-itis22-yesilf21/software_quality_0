"""Phase 2 / Step 8 helper.

Applies a JNose-guided manual smell checklist to each of the four
BookScanIntegrationTest.java files. JNose itself is not run (same
reason as Phase 1: no jnose CLI / Docker runtime available, and the
LLM A suites use plain main drivers rather than annotated JUnit
test classes). Instead, we compute structural metrics with regular
expressions so the per-variant claims in
bookscan/reports/test_smell_findings.md are quantitative.

Metrics emitted per file:

  test_methods                       count of @Test methods (JUnit 5
                                     style) OR top-level check* /
                                     requirement* methods called from
                                     main (main-driver style).
  assertion_calls                    total assertEquals/assertTrue/
                                     assertFalse/assertNotNull/
                                     assertNotEquals + custom helpers
                                     (assertListEquals, assertMapEquals,
                                     assertWordInfo) + check(...) calls.
  asserts_without_message            assertions whose argument list
                                     does NOT end with a String literal
                                     or a String-valued expression
                                     (rough JNose Assertion Roulette
                                     heuristic).
  longest_test_assertions            largest number of assertions in
                                     any single test method (Eager Test
                                     proxy).
  conditional_branches_in_tests      occurrences of `if (...)` inside
                                     test bodies (Conditional Test
                                     Logic).
  magic_int_literals_in_tests        occurrences of integer literals
                                     >= 2 inside test bodies that are
                                     not constants (Magic Number Test
                                     proxy; 0 and 1 are excluded
                                     because they are too common to
                                     flag).
  shared_fixture_field               1 if the test class declares a
                                     non-static BookScan field, else 0
                                     (General Fixture proxy).

The output is plain JSON to stdout. The findings markdown imports
these numbers verbatim into its per-variant tables.

Usage:
    python bookscan/analyze_test_smells.py
"""
from __future__ import annotations
import io, json, re, sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "bookscan"

VARIANTS = [
    ("llm_a/unmodified", "GPT-5.5",        "unmodified+combined", "main"),
    ("llm_b/unmodified", "Gemini 3.1 Pro", "unmodified+combined", "junit5"),
    ("llm_a/edited",     "GPT-5.5",        "edited+combined",     "main"),
    ("llm_b/edited",     "Gemini 3.1 Pro", "edited+combined",     "junit5"),
]

ASSERTION_PATTERNS = [
    r"\bassertEquals\b", r"\bassertTrue\b", r"\bassertFalse\b",
    r"\bassertNotNull\b", r"\bassertNotEquals\b",
    r"\bassertListEquals\b", r"\bassertMapEquals\b",
    r"\bassertWordInfo\b",
    r"\bcheck\(",
]
ASSERTION_RE = re.compile("|".join(ASSERTION_PATTERNS))


def _split_methods(source: str, framework: str) -> list[tuple[str, str]]:
    """Yield (method_name, body_source) for each test method.

    For junit5: methods preceded by @Test.
    For main: top-level private/static methods called from main(),
    matched by the names actually referenced inside main().
    """
    methods = []
    if framework == "junit5":
        for m in re.finditer(
            r"@Test\s+(?:public\s+|private\s+|void\s+|\s)*\bvoid\s+(\w+)\s*\(\s*\)\s*\{",
            source,
        ):
            name = m.group(1)
            body = _balanced_body(source, m.end() - 1)
            methods.append((name, body))
    else:  # main driver style
        # Pull the bodies of every method named requirementN_* or check*.
        for m in re.finditer(
            r"(?:private|public)\s+(?:static\s+)?void\s+"
            r"((?:requirement|check)\w+)\s*\(\s*[^)]*\)\s*\{",
            source,
        ):
            name = m.group(1)
            body = _balanced_body(source, m.end() - 1)
            methods.append((name, body))
    return methods


def _balanced_body(source: str, open_brace_idx: int) -> str:
    depth = 0
    start = open_brace_idx
    for i in range(open_brace_idx, len(source)):
        c = source[i]
        if c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return source[start + 1:i]
    return source[start + 1:]


def _strip_strings_and_comments(s: str) -> str:
    s = re.sub(r"//.*", "", s)
    s = re.sub(r"/\*.*?\*/", "", s, flags=re.DOTALL)
    s = re.sub(r'"(?:\\.|[^"\\])*"', '""', s)
    s = re.sub(r"'(?:\\.|[^'\\])'", "''", s)
    return s


def _last_arg_is_string_literal(call_text: str) -> bool:
    # crude but workable: look for the final ", \"..." before closing )
    # or for failure-message keywords like "Requirement N failed:".
    if '"' not in call_text:
        return False
    last_quote = call_text.rfind('"')
    open_q = call_text.rfind('"', 0, last_quote)
    if open_q == -1:
        return False
    snippet = call_text[open_q:last_quote + 1]
    return len(snippet) > 5  # ignore empty / one-char strings


def _count_asserts(body: str) -> tuple[int, int]:
    """Return (total assertion calls, assertion calls without message)."""
    matches = list(ASSERTION_RE.finditer(body))
    total = len(matches)
    without_msg = 0
    for m in matches:
        # Pull the whole call up to balanced close-paren.
        call_text = _extract_call(body, m.start())
        if not _last_arg_is_string_literal(call_text):
            without_msg += 1
    return total, without_msg


def _extract_call(text: str, start: int) -> str:
    open_idx = text.find("(", start)
    if open_idx == -1:
        return ""
    depth = 0
    for i in range(open_idx, len(text)):
        if text[i] == "(":
            depth += 1
        elif text[i] == ")":
            depth -= 1
            if depth == 0:
                return text[start:i + 1]
    return text[start:]


def _count_magic_ints(body: str) -> int:
    """Count integer literals >= 2 outside string/comment context."""
    cleaned = _strip_strings_and_comments(body)
    return sum(1 for _ in re.finditer(r"\b(\d+)\b", cleaned) if int(_.group(1)) >= 2)


def _count_conditionals(body: str) -> int:
    cleaned = _strip_strings_and_comments(body)
    return len(re.findall(r"\bif\s*\(", cleaned))


def _has_shared_fixture(source: str) -> bool:
    return bool(re.search(
        r"private\s+(?:final\s+)?BookScan\s+\w+\s*=\s*new\s+BookScan\s*\(\s*\)\s*;",
        source,
    ))


def analyse(path: Path, framework: str) -> dict:
    src = path.read_text(encoding="utf-8")
    methods = _split_methods(src, framework)
    per_method = []
    total_asserts = 0
    total_no_msg = 0
    total_cond = 0
    total_magic = 0
    longest = 0
    for name, body in methods:
        a_total, a_no_msg = _count_asserts(body)
        cond = _count_conditionals(body)
        magic = _count_magic_ints(body)
        per_method.append({
            "name": name,
            "asserts": a_total,
            "asserts_without_message": a_no_msg,
            "conditionals": cond,
            "magic_int_literals": magic,
        })
        total_asserts += a_total
        total_no_msg += a_no_msg
        total_cond += cond
        total_magic += magic
        longest = max(longest, a_total)
    return {
        "file": str(path.relative_to(ROOT)),
        "lines": len(src.splitlines()),
        "test_methods": len(methods),
        "assertion_calls": total_asserts,
        "asserts_without_message": total_no_msg,
        "longest_test_assertions": longest,
        "conditional_branches_in_tests": total_cond,
        "magic_int_literals_in_tests": total_magic,
        "shared_fixture_field": 1 if _has_shared_fixture(src) else 0,
        "per_method": per_method,
    }


def main() -> int:
    out = {"variants": []}
    for rel_path, llm, prompt_variant, framework in VARIANTS:
        path = BOOK / rel_path / "BookScanIntegrationTest.java"
        a = analyse(path, framework)
        a.update({"llm": llm, "prompt_variant": prompt_variant,
                  "framework": framework, "variant": rel_path})
        out["variants"].append(a)
    print(json.dumps(out, indent=2, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    sys.exit(main())
