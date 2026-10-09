package com.cartnova.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password hashing and verification utility using BCrypt.
 * Secures user passwords before storing them in the database.
 */
public class PasswordUtil {

    private static final int BCRYPT_COST = 10;

    /**
     * Hashes a plain text password using BCrypt algorithm.
     *
     * @param plainPassword Plain text password
     * @return Hashed password string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Verifies a plain text password against a BCrypt hashed password.
     *
     * @param plainPassword Plain text password
     * @param hashedPassword Stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
