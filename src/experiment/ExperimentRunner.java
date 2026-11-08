package experiment;

import dp.EditDistance;
import dp.LongestCommonSubsequence;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;
import matching.KmpMatcher;
import matching.NaiveMatcher;
import matching.RabinKarpMatcher;
import matching.StringMatcher;
import suffix.KasaiLcp;
import suffix.SuffixArrayBuilder;
import util.OperationCounter;

public class ExperimentRunner {
    private static final long SEED = 42L;
    private static final int RUNS = 5;
    private static final long WARM_UP_NANOS = 500_000_000L;
    private static final int RANDOM_PATTERN_LENGTH = 20;
    private static final int REPETITIVE_PATTERN_LENGTH = 1000;
    private static final int[] DP_SIZES = {100, 500, 1000};
    private static final int[] TEXT_SIZES = {10000, 50000, 100000};
    private static final int[] KASAI_SIZES = {1000, 5000, 10000};
    private static final String RUN_COLUMNS = "run1_ms,run2_ms,run3_ms,run4_ms,run5_ms,avg_ms";

    private static long sink = 0;

    private interface Task {
        long run(OperationCounter counter);
    }

    private static class Measurement {
        double[] runsMs = new double[RUNS];
        double averageMs;
        long result;
        OperationCounter counter;

        String timesAsCsv() {
            StringBuilder line = new StringBuilder();
            for (double run : runsMs) {
                line.append(String.format(Locale.US, "%.4f,", run));
            }
            line.append(String.format(Locale.US, "%.4f", averageMs));
            return line.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("results"));
        runDynamicProgramming();
        runStringMatching();
        runKasai();
        System.out.println("Finished. Control value: " + sink);
    }

    private static Measurement measure(Task task) {
        long warmUpEnd = System.nanoTime() + WARM_UP_NANOS;
        while (System.nanoTime() < warmUpEnd) {
            sink += task.run(new OperationCounter());
        }

        Measurement measurement = new Measurement();
        double total = 0;
        for (int i = 0; i < RUNS; i++) {
            OperationCounter counter = new OperationCounter();
            long start = System.nanoTime();
            long result = task.run(counter);
            long end = System.nanoTime();
            sink += result;
            measurement.runsMs[i] = (end - start) / 1_000_000.0;
            total += measurement.runsMs[i];
            measurement.result = result;
            measurement.counter = counter;
        }
        measurement.averageMs = total / RUNS;
        return measurement;
    }

    private static void runDynamicProgramming() throws IOException {
        Random random = new Random(SEED);
        try (PrintWriter out = new PrintWriter("results/dp.csv")) {
            out.println("algorithm,n,m,result," + RUN_COLUMNS + ",char_comparisons,cell_updates");
            for (int size : DP_SIZES) {
                String first = StringGenerator.random(size, random);
                String second = StringGenerator.random(size, random);
                Measurement lcs = measure(counter -> LongestCommonSubsequence.length(first, second, counter));
                Measurement edit = measure(counter -> EditDistance.distance(first, second, counter));
                writeDpRow(out, "LCS", size, lcs);
                writeDpRow(out, "Edit Distance", size, edit);
            }
        }
    }

    private static void writeDpRow(PrintWriter out, String name, int size, Measurement m) {
        out.println(name + "," + size + "," + size + "," + m.result + "," + m.timesAsCsv() + ","
            + m.counter.charComparisons + "," + m.counter.cellUpdates);
        System.out.println(name + " n=" + size + " average " + m.averageMs + " ms");
    }

    private static void runStringMatching() throws IOException {
        Random random = new Random(SEED);
        StringMatcher[] matchers = {new NaiveMatcher(), new KmpMatcher(), new RabinKarpMatcher()};
        try (PrintWriter out = new PrintWriter("results/matching.csv")) {
            out.println("scenario,algorithm,text_length,pattern_length,occurrences," + RUN_COLUMNS
                + ",char_comparisons,hash_comparisons,spurious_hits");
            for (int size : TEXT_SIZES) {
                String text = StringGenerator.random(size, random);
                String present = text.substring(size / 2, size / 2 + RANDOM_PATTERN_LENGTH);
                String absent = absentPattern(text, random);
                String flatText = StringGenerator.repeated('a', size);
                String flatPresent = StringGenerator.repeated('a', REPETITIVE_PATTERN_LENGTH);
                String flatAbsent = StringGenerator.repeated('a', REPETITIVE_PATTERN_LENGTH - 1) + "b";

                runScenario(out, "random_present", text, present, matchers);
                runScenario(out, "random_absent", text, absent, matchers);
                runScenario(out, "repetitive_present", flatText, flatPresent, matchers);
                runScenario(out, "repetitive_absent", flatText, flatAbsent, matchers);
            }
        }
    }

    private static String absentPattern(String text, Random random) {
        NaiveMatcher checker = new NaiveMatcher();
        String pattern = StringGenerator.random(RANDOM_PATTERN_LENGTH, random);
        while (!checker.search(text, pattern, new OperationCounter()).isEmpty()) {
            pattern = StringGenerator.random(RANDOM_PATTERN_LENGTH, random);
        }
        return pattern;
    }

    private static void runScenario(PrintWriter out, String scenario, String text, String pattern, StringMatcher[] matchers) {
        for (StringMatcher matcher : matchers) {
            Measurement m = measure(counter -> matcher.search(text, pattern, counter).size());
            out.println(scenario + "," + matcher.name() + "," + text.length() + "," + pattern.length() + ","
                + m.result + "," + m.timesAsCsv() + "," + m.counter.charComparisons + ","
                + m.counter.hashComparisons + "," + m.counter.spuriousHits);
            System.out.println(scenario + " " + matcher.name() + " n=" + text.length() + " average " + m.averageMs + " ms");
        }
    }

    private static void runKasai() throws IOException {
        Random random = new Random(SEED);
        try (PrintWriter out = new PrintWriter("results/kasai.csv")) {
            out.println("input_type,n," + RUN_COLUMNS + ",char_comparisons");
            for (int size : KASAI_SIZES) {
                writeKasaiRow(out, "random", StringGenerator.random(size, random));
                writeKasaiRow(out, "repetitive", StringGenerator.repeated('a', size));
            }
        }
    }

    private static void writeKasaiRow(PrintWriter out, String type, String text) {
        int[] suffixArray = SuffixArrayBuilder.build(text);
        Measurement m = measure(counter -> KasaiLcp.build(text, suffixArray, counter).length);
        out.println(type + "," + text.length() + "," + m.timesAsCsv() + "," + m.counter.charComparisons);
        System.out.println("Kasai " + type + " n=" + text.length() + " average " + m.averageMs + " ms");
    }
}
