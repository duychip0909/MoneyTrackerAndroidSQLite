package com.example.moneytrackerandroidsqlite.utils;

import android.os.Build;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility class for handling security-related operations including password hashing and verification.
 */
public class SecurityUtils {
    private static final String TAG = "SecurityUtils";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // bytes
    private static final int ITERATIONS = 10000;

    /**
     * Verifies a plaintext password against a stored password hash
     *
     * @param plainPassword The plaintext password to verify
     * @param storedHash The stored password hash (format: "iterations:salt:hash")
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }

        try {
            // Split the hash into its components
            String[] parts = storedHash.split(":");
            if (parts.length != 3) {
                Log.e(TAG, "Invalid hash format. Expected format: iterations:salt:hash");
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                salt = Base64.getDecoder().decode(parts[1]);
            }
            byte[] hash = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                hash = Base64.getDecoder().decode(parts[2]);
            }

            // Generate hash of the provided plaintext password using the same salt and iterations
            byte[] testHash = hashPassword(plainPassword, salt, iterations);

            // Compare the hashes using a constant-time comparison method
            return constantTimeEquals(hash, testHash);

        } catch (Exception e) {
            Log.e(TAG, "Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hashes a password using SHA-256 with the specified salt and iterations
     *
     * @param password The password to hash
     * @param salt The salt to use
     * @param iterations Number of iterations to perform
     * @return The hashed password
     */
    private static byte[] hashPassword(String password, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.reset();
            digest.update(salt);
            byte[] input = digest.digest(password.getBytes());

            for (int i = 0; i < iterations; i++) {
                digest.reset();
                input = digest.digest(input);
            }

            return input;

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password: " + e.getMessage());
            throw new RuntimeException("Failed to hash password", e);
        }
    }

//    /**
//     * Creates a hash from a plaintext password (for storing in database)
//     *
//     * @param plainPassword The plaintext password to hash
//     * @return A formatted string containing iterations, salt, and hash
//     */
//    public static String hashPasswordForStorage(String plainPassword) {
//        try {
//            // Generate a random salt
//            SecureRandom random = new SecureRandom();
//            byte[] salt = new byte[SALT_LENGTH];
//            random.nextBytes(salt);
//
//            // Hash the password
//            byte[] hash = hashPassword(plainPassword, salt, ITERATIONS);
//
//            // Format as iterations:salt:hash (all base64 encoded)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return ITERATIONS + ":" +
//                        Base64.getEncoder().encodeToString(salt) + ":" +
//                        Base64.getEncoder().encodeToString(hash);
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error creating password hash: " + e.getMessage());
//            throw new RuntimeException("Failed to hash password for storage", e);
//        }
//    }

    /**
     * Constant-time comparison of two byte arrays to prevent timing attacks
     *
     * @param a First byte array
     * @param b Second byte array
     * @return true if arrays are equal, false otherwise
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i]; // XOR will be 0 only when bits are the same
        }

        return result == 0;
    }
}