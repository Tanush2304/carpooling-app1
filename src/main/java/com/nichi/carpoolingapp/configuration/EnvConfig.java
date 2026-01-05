package com.nichi.carpoolingapp.configuration;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    public static Dotenv getEnv() {
        return Dotenv.load();
    }
}

