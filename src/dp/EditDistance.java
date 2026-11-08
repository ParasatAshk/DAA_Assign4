package dp;

import util.OperationCounter;

public class EditDistance {

    public static int distance(String source, String target) {
        return distance(source, target, new OperationCounter());
    }

    public static int distance(String source, String target, OperationCounter counter) {
        int n = source.length();
        int m = target.length();
        int[][] table = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            table[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            table[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                counter.charComparisons++;
                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    table[i][j] = table[i - 1][j - 1];
                } else {
                    int deletion = table[i - 1][j];
                    int insertion = table[i][j - 1];
                    int substitution = table[i - 1][j - 1];
                    table[i][j] = 1 + Math.min(deletion, Math.min(insertion, substitution));
                }
                counter.cellUpdates++;
            }
        }

        return table[n][m];
    }
}
