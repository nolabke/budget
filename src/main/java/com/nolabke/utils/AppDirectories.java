package com.nolabke.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppDirectories {

    private AppDirectories() {}

    public static Path getDataDirectory() {

        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) {

            String localAppData = System.getenv("LOCALAPPDATA");

            if (localAppData != null) {
                return Paths.get(localAppData, "Budget");
            }
        }

        if (os.contains("mac")) {
            return Paths.get(
                    home,
                    "Library",
                    "Application Support",
                    "Budget"
            );
        }

        // Linux (XDG)
        return Paths.get(
                home,
                ".local",
                "share",
                "Budget"
        );
    }
}
