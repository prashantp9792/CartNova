package com.cartnova.util;

import java.util.regex.Pattern;

/**
 * Server-side input validation and sanitization utility.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Phone is optional
        }
        return PHONE_PATTERN.matcher(phone.trim().replaceAll("[\\s\\-\\(\\)]", "")).matches();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }

    public static boolean isNonNegative(int number) {
        return number >= 0;
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
