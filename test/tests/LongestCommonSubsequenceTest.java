package tests;

import dp.LongestCommonSubsequence;
import util.OperationCounter;

public class LongestCommonSubsequenceTest {

    public static void run() {
        TestSupport.checkEquals("LCS identical strings", 6, LongestCommonSubsequence.length("abcdab", "abcdab"));
        TestSupport.checkEquals("LCS completely different strings", 0, LongestCommonSubsequence.length("aaaa", "bbbb"));
        TestSupport.checkEquals("LCS first string empty", 0, LongestCommonSubsequence.length("", "abc"));
        TestSupport.checkEquals("LCS second string empty", 0, LongestCommonSubsequence.length("abc", ""));
        TestSupport.checkEquals("LCS both strings empty", 0, LongestCommonSubsequence.length("", ""));
        TestSupport.checkEquals("LCS textbook example", 4, LongestCommonSubsequence.length("ABCBDAB", "BDCABA"));
        TestSupport.checkEquals("LCS subsequence is not contiguous", 3, LongestCommonSubsequence.length("axbxc", "abc"));

        OperationCounter counter = new OperationCounter();
        LongestCommonSubsequence.length("abcd", "abc", counter);
        TestSupport.checkTrue("LCS fills n times m cells", counter.cellUpdates == 12);
    }
}
