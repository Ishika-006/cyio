package com.ishika.foodwaste.jpa.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // 🔐 Hash plain text password
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // 🔐 Verify password against hashed password
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
