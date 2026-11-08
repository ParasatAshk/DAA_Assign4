# Assignment 4: Text Analysis with Dynamic Programming and String Algorithms

This repository contains a Java application for text analysis. It implements two dynamic programming algorithms, three string matching algorithms, and Kasai's algorithm for the Longest Common Prefix (LCP) array. The project compares these algorithms by theoretical complexity and by experiments. This README is the full report.

## Repository Structure

| Path | Content |
|---|---|
| `src/dp` | LCS and Edit Distance |
| `src/matching` | Matcher interface, Naive, KMP, and Rabin-Karp |
| `src/suffix` | Kasai LCP algorithm and a simple suffix array builder |
| `src/util` | Operation counter |
| `src/app` | Small demo application |
| `src/experiment` | String generator, experiment runner, and plot generator |
| `test/tests` | Tests for all required algorithms |
| `results` | Experimental results in CSV tables |
| `results/plots` | Plots in PNG format |

## How to Compile and Run

The project needs only a JDK. No external library is used. The code was compiled and run with OpenJDK 25.0.2, and it also compiles with `--release 17`. Run all commands from the repository root. The first command uses a Unix shell, for example macOS, Linux, or Git Bash on Windows.

```bash
javac -d out $(find src test -name "*.java")
java -cp out tests.AllTests
java -cp out app.Main
java -cp out experiment.ExperimentRunner
java -cp out experiment.PlotGenerator
```

| Command | Purpose |
|---|---|
| `tests.AllTests` | Runs all tests. The exit code is 1 if a test fails. |
| `app.Main` | Shows all algorithms on short example strings. |
| `experiment.ExperimentRunner` | Runs all experiments (about 30 seconds) and writes CSV files to `results`. |
| `experiment.PlotGenerator` | Reads the CSV files and writes PNG plots to `results/plots`. |

## 1. Problem Description and Algorithm Overview

Text analysis often needs two kinds of answers. The first kind measures how similar two strings are. The second kind finds where a pattern occurs in a text. This project also studies the repeated structure of one string by using the LCP array.

In this report, `n` is the length of the first string or the text, and `m` is the length of the second string or the pattern.

### 1.1 Longest Common Subsequence (LCS)

LCS finds the length of the longest sequence of characters that appears in both strings in the same order. The characters do not need to be adjacent.

Let `L[i][j]` be the LCS length of the first `i` characters of `A` and the first `j` characters of `B`.

- Base cases: `L[i][0] = 0` and `L[0][j] = 0`.
- If `A[i-1] == B[j-1]`, then `L[i][j] = L[i-1][j-1] + 1`.
- Otherwise, `L[i][j] = max(L[i-1][j], L[i][j-1])`.

**Why the recurrence is correct.** An empty prefix has no common subsequence, so the base cases are correct. If the last characters are equal, an optimal solution can always use this pair: any optimal solution that does not use it can be changed to use it without becoming shorter. The rest of the solution is an optimal solution for the two shorter prefixes. If the last characters are different, they cannot both be the last matched pair. So at least one of them is not used, and the best answer is the better of the two cases that remove one character. The table is filled so that all smaller prefixes are solved first.

### 1.2 Edit Distance (Levenshtein Distance)

Edit Distance finds the minimum number of insertions, deletions, and substitutions that transform one string into another.

Let `D[i][j]` be the distance from the first `i` characters of `S` to the first `j` characters of `T`.

- Base cases: `D[i][0] = i` and `D[0][j] = j`.
- If `S[i-1] == T[j-1]`, then `D[i][j] = D[i-1][j-1]`.
- Otherwise, `D[i][j] = 1 + min(D[i-1][j], D[i][j-1], D[i-1][j-1])`. The three values represent deletion, insertion, and substitution.

**Why the recurrence is correct.** To transform a prefix into an empty string, we must delete all its characters. To transform an empty string into a prefix, we must insert all its characters. For other cases, consider the last operation on the last position of an optimal edit sequence. It is a deletion, an insertion, a substitution, or no operation when the characters are already equal. These cases cover all possibilities, and each case leaves a smaller subproblem that must also be solved optimally. If the characters are equal, keeping them is never worse than any other option, because the distances of neighbouring prefixes differ by at most 1.

### 1.3 Naive String Matching

The Naive algorithm tries every start position from `0` to `n - m`. At each position, it compares the characters from left to right and stops at the first mismatch. It is the baseline for comparison.

### 1.4 Knuth-Morris-Pratt (KMP)

KMP first builds the LPS table. The value `lps[i]` is the length of the longest proper prefix of `pattern[0..i]` that is also a suffix of this part of the pattern.

**How KMP avoids repeated comparisons.** During the search, the text index `i` never moves backward. When a mismatch happens after `j` matched characters, these `j` text characters are already known, because they are equal to the first `j` pattern characters. The value `lps[j-1]` shows the longest prefix of the pattern that already matches the end of this text part. So KMP continues from `j = lps[j-1]` and does not compare these characters again. After a full match, it continues from `lps[m-1]`, so overlapping occurrences are also found.

### 1.5 Rabin-Karp

Rabin-Karp compares a hash of the pattern with a hash of each text window of length `m`. The next window hash is computed from the previous one in constant time with a rolling hash:

`hash(next) = ((hash(current) - text[start] * base^(m-1)) * base + text[start+m]) mod q`

This project uses `base = 256` and the prime modulus `q = 1,000,000,007`. The calculations use `long`, so they do not overflow.

**How Rabin-Karp handles hash collisions.** Two different strings can have the same hash. For this reason, equal hashes are only a sign of a possible match. The algorithm then compares the window with the pattern character by character, and it reports the position only if all characters are equal. A window with an equal hash but different characters is a spurious hit. It costs extra time, but it cannot produce a wrong answer. One test uses the very small modulus 3 to force collisions and confirms that the result stays correct.

### 1.6 Kasai's LCP Algorithm

A suffix array stores the start positions of all suffixes of a string in sorted order. The LCP array stores the length of the common prefix of neighbouring suffixes in this order. In this project, `lcp[i]` is the LCP of the suffixes at `SA[i]` and `SA[i-1]`, and `lcp[0] = 0`. Large LCP values show repeated substrings in the string.

Kasai's algorithm uses the rank of each suffix and processes the suffixes in text order. If the suffix at position `i` has an LCP of `h` with its predecessor, then the suffix at position `i + 1` has an LCP of at least `h - 1` with its own predecessor. This holds because removing the first character from both suffixes keeps their order and leaves `h - 1` common characters. Therefore, the algorithm does not start the comparison from zero. It continues from `h - 1`.

Manual example for `banana`:

| i | SA[i] | Suffix | LCP with previous suffix |
|---|---|---|---|
| 0 | 5 | a | 0 (defined) |
| 1 | 3 | ana | 1 (a) |
| 2 | 1 | anana | 3 (ana) |
| 3 | 0 | banana | 0 |
| 4 | 4 | na | 0 |
| 5 | 2 | nana | 2 (na) |
