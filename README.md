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
