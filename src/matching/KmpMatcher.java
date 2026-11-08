package matching;

import java.util.ArrayList;
import java.util.List;
import util.OperationCounter;

public class KmpMatcher implements StringMatcher {

    @Override
    public String name() {
        return "KMP";
    }

    public static int[] buildLps(String pattern, OperationCounter counter) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0;
        int i = 1;

        while (i < m) {
            counter.charComparisons++;
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else if (length > 0) {
                length = lps[length - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }

        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern, OperationCounter counter) {
        List<Integer> occurrences = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return occurrences;
        }

        int[] lps = buildLps(pattern, counter);
        int i = 0;
        int j = 0;

        while (i < n) {
            counter.charComparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    occurrences.add(i - m);
                    j = lps[m - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }

        return occurrences;
    }
}
