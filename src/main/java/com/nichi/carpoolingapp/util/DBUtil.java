package com.nichi.carpoolingapp.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    private static final String URL = ConfigManager.get("db.url");
    private static final String USER = ConfigManager.get("db.user");
    private static final String PASS = ConfigManager.get("db.password");

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
