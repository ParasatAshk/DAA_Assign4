package tests;

public class AllTests {

    public static void main(String[] args) {
        LongestCommonSubsequenceTest.run();
        EditDistanceTest.run();
        StringMatchingTest.run();
        KasaiLcpTest.run();

        System.out.println();
        System.out.println("Passed: " + TestSupport.passedCount());
        System.out.println("Failed: " + TestSupport.failedCount());

        if (TestSupport.failedCount() > 0) {
            System.exit(1);
        }
    }
}
