package com.abhishek.tenanthub.common.util;

import java.security.SecureRandom;

/**
 * Generates one-time initial passwords for accounts created by an administrator.
 *
 * @author Abhishek Raj
 */
public final class PasswordUtils {

    // No look-alike characters (0/O, 1/l/I) so passwords can be read out or typed safely
    private static final String LETTERS = "abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String DIGITS = "23456789";
    private static final String ALL = LETTERS + DIGITS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtils() {
    }

    /**
     * A random password of the given length (minimum 8) containing at least one letter and one digit.
     */
    public static String random(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8");
        }
        var chars = new char[length];
        chars[0] = LETTERS.charAt(RANDOM.nextInt(LETTERS.length()));
        chars[1] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        for (int i = 2; i < length; i++) {
            chars[i] = ALL.charAt(RANDOM.nextInt(ALL.length()));
        }
        // Shuffle so the guaranteed letter/digit are not always first
        for (int i = length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            var tmp = chars[i];
            chars[i] = chars[j];
            chars[j] = tmp;
        }
        return new String(chars);
    }

    /**
     * A random 12-character initial password.
     */
    public static String random() {
        return random(12);
    }
}
