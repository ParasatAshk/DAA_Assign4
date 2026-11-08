package dp;

import util.OperationCounter;

public class LongestCommonSubsequence {

    public static int length(String first, String second) {
        return length(first, second, new OperationCounter());
    }

    public static int length(String first, String second, OperationCounter counter) {
        int n = first.length();
        int m = second.length();
        int[][] table = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            table[i][0] = 0;
        }
        for (int j = 0; j <= m; j++) {
            table[0][j] = 0;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                counter.charComparisons++;
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1] + 1;
                } else {
                    table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]);
                }
                counter.cellUpdates++;
            }
        }

        return table[n][m];
    }
}
