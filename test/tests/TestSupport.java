package tests;

import java.util.Arrays;
import java.util.List;

public class TestSupport {
    private static int passed = 0;
    private static int failed = 0;

    public static void checkEquals(String name, int expected, int actual) {
        report(name, expected == actual, String.valueOf(expected), String.valueOf(actual));
    }

    public static void checkEquals(String name, List<Integer> expected, List<Integer> actual) {
        report(name, expected.equals(actual), expected.toString(), actual.toString());
    }

    public static void checkEquals(String name, int[] expected, int[] actual) {
        report(name, Arrays.equals(expected, actual), Arrays.toString(expected), Arrays.toString(actual));
    }

    public static void checkTrue(String name, boolean condition) {
        report(name, condition, "true", String.valueOf(condition));
    }

    public static int passedCount() {
        return passed;
    }

    public static int failedCount() {
        return failed;
    }

    private static void report(String name, boolean success, String expected, String actual) {
        if (success) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name + " expected " + expected + " but was " + actual);
        }
    }
}
