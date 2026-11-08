package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import matching.KmpMatcher;
import matching.NaiveMatcher;
import matching.RabinKarpMatcher;
import matching.StringMatcher;
import util.OperationCounter;

public class StringMatchingTest {
    private static final StringMatcher[] MATCHERS = {
        new NaiveMatcher(), new KmpMatcher(), new RabinKarpMatcher()
    };

    public static void run() {
        checkAll("pattern at beginning", "abcdefgh", "abc", List.of(0));
        checkAll("pattern in middle", "abcdefgh", "def", List.of(3));
        checkAll("pattern at end", "abcdefgh", "fgh", List.of(5));
        checkAll("multiple occurrences", "abcabcabc", "abc", List.of(0, 3, 6));
        checkAll("overlapping occurrences", "aaaaa", "aa", List.of(0, 1, 2, 3));
        checkAll("overlapping with border", "abababa", "aba", List.of(0, 2, 4));
        checkAll("pattern absent", "abcdefgh", "xyz", List.of());
        checkAll("pattern longer than text", "abc", "abcd", List.of());
        checkAll("empty pattern", "abc", "", List.of());
        checkAll("empty text", "", "abc", List.of());
        checkAll("empty text and pattern", "", "", List.of());
        checkAll("pattern equals text", "abcd", "abcd", List.of(0));
        checkAll("single character", "abca", "a", List.of(0, 3));

        testLpsTable();
        testHashCollisions();
        testRandomInputsAgree();
    }

    private static void checkAll(String name, String text, String pattern, List<Integer> expected) {
        List<List<Integer>> results = new ArrayList<>();
        for (StringMatcher matcher : MATCHERS) {
            List<Integer> actual = matcher.search(text, pattern, new OperationCounter());
            results.add(actual);
            TestSupport.checkEquals(matcher.name() + " " + name, expected, actual);
        }
        boolean same = results.get(0).equals(results.get(1)) && results.get(1).equals(results.get(2));
        TestSupport.checkTrue("All matchers agree: " + name, same);
    }

    private static void testLpsTable() {
        int[] actual = KmpMatcher.buildLps("aabaaab", new OperationCounter());
        TestSupport.checkEquals("KMP LPS table", new int[] {0, 1, 0, 1, 2, 2, 3}, actual);
    }

    private static void testHashCollisions() {
        RabinKarpMatcher weakHash = new RabinKarpMatcher(256, 3);
        OperationCounter counter = new OperationCounter();
        List<Integer> actual = weakHash.search("abcdabcdabcd", "bcd", counter);
        TestSupport.checkEquals("Rabin-Karp correct under weak hash", List.of(1, 5, 9), actual);
        TestSupport.checkTrue("Rabin-Karp weak hash causes spurious hits", counter.spuriousHits > 0);
    }

    private static void testRandomInputsAgree() {
        Random random = new Random(42);
        boolean allAgree = true;

        for (int round = 0; round < 500; round++) {
            String text = randomString(random, random.nextInt(200));
            String pattern = randomString(random, 1 + random.nextInt(4));
            List<Integer> naive = MATCHERS[0].search(text, pattern, new OperationCounter());
            List<Integer> kmp = MATCHERS[1].search(text, pattern, new OperationCounter());
            List<Integer> rabinKarp = MATCHERS[2].search(text, pattern, new OperationCounter());
            if (!naive.equals(kmp) || !naive.equals(rabinKarp)) {
                allAgree = false;
            }
        }

        TestSupport.checkTrue("All matchers agree on 500 random inputs", allAgree);
    }

    private static String randomString(Random random, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) ('a' + random.nextInt(2)));
        }
        return builder.toString();
    }
}
