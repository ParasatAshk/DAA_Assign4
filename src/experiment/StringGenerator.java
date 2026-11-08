package experiment;

import java.util.Random;

public class StringGenerator {
    public static final String ALPHABET = "abcd";

    public static String random(int length, Random random) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }

    public static String repeated(char letter, int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(letter);
        }
        return builder.toString();
    }
}
