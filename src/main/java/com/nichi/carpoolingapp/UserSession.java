package com.nichi.carpoolingapp;

public class UserSession {

    private static String userName;
    private static String userEmail;

    private static int userId;

    public static void setUser(int id, String name, String email) {
        userId = id;
        userName = name;
        userEmail = email;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void clear() {
        userId = 0;
        userName = null;
        userEmail = null;
    }
}
