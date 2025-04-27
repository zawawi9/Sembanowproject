
package com.raven.form;

import java.time.LocalTime;

public class UserSession {
    private static String username;
    private static LocalTime loginTime;
    private static String role;

    // Getters
    public static String getUsername() {
        return username;
    }

    public static LocalTime getLoginTime() {
        return loginTime;
    }

    public static String getRole() {
        return role;
    }

    // Setters
    public static void setUsername(String username) {
        UserSession.username = username;
    }

    public static void setLoginTime(LocalTime loginTime) {
        UserSession.loginTime = loginTime;
    }

    public static void setRole(String role) {
        UserSession.role = role;
    }

    // Method to clear session data (optional, for logout)
    public static void clearSession() {
        username = null;
        loginTime = null;
        role = null;
    }
}
