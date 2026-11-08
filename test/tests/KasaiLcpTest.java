package tests;

import java.util.Random;
import suffix.KasaiLcp;
import suffix.SuffixArrayBuilder;

public class KasaiLcpTest {

    public static void run() {
        checkManualExample("banana", new int[] {5, 3, 1, 0, 4, 2}, new int[] {0, 1, 3, 0, 0, 2});
        checkManualExample("abracadabra",
            new int[] {10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2},
            new int[] {0, 1, 4, 1, 1, 0, 3, 0, 0, 0, 2});
        checkManualExample("aaaa", new int[] {3, 2, 1, 0}, new int[] {0, 1, 2, 3});
        checkManualExample("a", new int[] {0}, new int[] {0});
        checkManualExample("", new int[] {}, new int[] {});
        testRandomInputs();
    }

    private static void checkManualExample(String text, int[] suffixArray, int[] expectedLcp) {
        String label = "\"" + text + "\"";
        TestSupport.checkEquals("Suffix array builder on " + label, suffixArray, SuffixArrayBuilder.build(text));
        TestSupport.checkEquals("Kasai LCP on " + label, expectedLcp, KasaiLcp.build(text, suffixArray));
    }

    private static void testRandomInputs() {
        Random random = new Random(42);
        boolean allCorrect = true;

        for (int round = 0; round < 200; round++) {
            StringBuilder builder = new StringBuilder();
            int length = 1 + random.nextInt(100);
            for (int i = 0; i < length; i++) {
                builder.append((char) ('a' + random.nextInt(3)));
            }
            String text = builder.toString();
            int[] suffixArray = SuffixArrayBuilder.build(text);
            int[] lcp = KasaiLcp.build(text, suffixArray);

            for (int i = 1; i < length; i++) {
                if (lcp[i] != directLcp(text, suffixArray[i], suffixArray[i - 1])) {
                    allCorrect = false;
                }
            }
        }

        TestSupport.checkTrue("Kasai matches direct LCP on 200 random strings", allCorrect);
    }

    private static int directLcp(String text, int first, int second) {
        int length = 0;
        while (first + length < text.length() && second + length < text.length()
                && text.charAt(first + length) == text.charAt(second + length)) {
            length++;
        }
        return length;
    }
}
