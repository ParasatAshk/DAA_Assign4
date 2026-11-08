package matching;

import java.util.ArrayList;
import java.util.List;
import util.OperationCounter;

public class NaiveMatcher implements StringMatcher {

    @Override
    public String name() {
        return "Naive";
    }

    @Override
    public List<Integer> search(String text, String pattern, OperationCounter counter) {
        List<Integer> occurrences = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return occurrences;
        }

        for (int start = 0; start <= n - m; start++) {
            int j = 0;
            while (j < m) {
                counter.charComparisons++;
                if (text.charAt(start + j) != pattern.charAt(j)) {
                    break;
                }
                j++;
            }
            if (j == m) {
                occurrences.add(start);
            }
        }

        return occurrences;
    }
}
