package com.nolabke.config;

import java.io.InputStream;
import java.util.Properties;

public final class AppInfo {


    private static final Properties properties = new Properties();


    static {
        try (InputStream input =
                     AppInfo.class.getResourceAsStream("/version.properties")) {

            if (input != null) {
                properties.load(input);
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot load application information",
                    e
            );
        }
    }


    private AppInfo() {
    }


    public static String getName() {
        return "Budget";
    }


    public static String getVersion() {
        return properties.getProperty(
                "app.version",
                "unknown"
        );
    }
}
