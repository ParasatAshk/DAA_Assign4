package app;

import dp.EditDistance;
import dp.LongestCommonSubsequence;
import java.util.Arrays;
import matching.KmpMatcher;
import matching.NaiveMatcher;
import matching.RabinKarpMatcher;
import matching.StringMatcher;
import suffix.KasaiLcp;
import suffix.SuffixArrayBuilder;
import util.OperationCounter;

public class Main {

    public static void main(String[] args) {
        String first = "algorithm";
        String second = "altruistic";
        System.out.println("First string: " + first);
        System.out.println("Second string: " + second);
        System.out.println("LCS length: " + LongestCommonSubsequence.length(first, second));
        System.out.println("Edit distance: " + EditDistance.distance(first, second));
        System.out.println();

        String text = "abracadabra";
        String pattern = "abra";
        System.out.println("Text: " + text);
        System.out.println("Pattern: " + pattern);
        StringMatcher[] matchers = {new NaiveMatcher(), new KmpMatcher(), new RabinKarpMatcher()};
        for (StringMatcher matcher : matchers) {
            System.out.println(matcher.name() + " occurrences: " + matcher.search(text, pattern, new OperationCounter()));
        }
        System.out.println();

        int[] suffixArray = SuffixArrayBuilder.build(text);
        System.out.println("Suffix array: " + Arrays.toString(suffixArray));
        System.out.println("LCP array: " + Arrays.toString(KasaiLcp.build(text, suffixArray)));
    }
}
