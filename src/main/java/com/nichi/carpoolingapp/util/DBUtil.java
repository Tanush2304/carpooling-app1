package com.nichi.carpoolingapp.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    private static final String URL =
            "jdbc:mysql://192.168.1.92:3306/poolcar";
    private static final String USER = "tanushkn";
    private static final String PASS = "Tanush@2304";

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
