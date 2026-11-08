package matching;

import java.util.ArrayList;
import java.util.List;
import util.OperationCounter;

public class RabinKarpMatcher implements StringMatcher {
    private static final long DEFAULT_BASE = 256;
    private static final long DEFAULT_MODULUS = 1_000_000_007L;

    private final long base;
    private final long modulus;

    public RabinKarpMatcher() {
        this(DEFAULT_BASE, DEFAULT_MODULUS);
    }

    public RabinKarpMatcher(long base, long modulus) {
        this.base = base;
        this.modulus = modulus;
    }

    @Override
    public String name() {
        return "Rabin-Karp";
    }

    @Override
    public List<Integer> search(String text, String pattern, OperationCounter counter) {
        List<Integer> occurrences = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return occurrences;
        }

        long highestPower = 1;
        for (int i = 0; i < m - 1; i++) {
            highestPower = (highestPower * base) % modulus;
        }

        long patternHash = 0;
        long windowHash = 0;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * base + pattern.charAt(i)) % modulus;
            windowHash = (windowHash * base + text.charAt(i)) % modulus;
        }

        for (int start = 0; start <= n - m; start++) {
            counter.hashComparisons++;
            if (windowHash == patternHash) {
                if (matchesAt(text, pattern, start, counter)) {
                    occurrences.add(start);
                } else {
                    counter.spuriousHits++;
                }
            }

            if (start < n - m) {
                long removed = (text.charAt(start) * highestPower) % modulus;
                windowHash = (windowHash - removed + modulus) % modulus;
                windowHash = (windowHash * base + text.charAt(start + m)) % modulus;
            }
        }

        return occurrences;
    }

    private boolean matchesAt(String text, String pattern, int start, OperationCounter counter) {
        for (int j = 0; j < pattern.length(); j++) {
            counter.charComparisons++;
            if (text.charAt(start + j) != pattern.charAt(j)) {
                return false;
            }
        }
        return true;
    }
}
