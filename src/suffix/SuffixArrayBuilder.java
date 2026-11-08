package suffix;

import java.util.Arrays;

public class SuffixArrayBuilder {

    public static int[] build(String text) {
        int n = text.length();
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }

        Arrays.sort(order, (first, second) -> compareSuffixes(text, first, second));

        int[] suffixArray = new int[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = order[i];
        }
        return suffixArray;
    }

    private static int compareSuffixes(String text, int first, int second) {
        int n = text.length();
        while (first < n && second < n) {
            if (text.charAt(first) != text.charAt(second)) {
                return text.charAt(first) - text.charAt(second);
            }
            first++;
            second++;
        }
        return second - first;
    }
}
