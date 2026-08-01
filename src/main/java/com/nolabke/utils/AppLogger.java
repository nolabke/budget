package com.nolabke.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public final class AppLogger {

    private static final Logger LOGGER =
            Logger.getLogger("NoLabke");

    private static final String LOG_FILE = "nolabke.log";

    static {
        configure();
    }

    private AppLogger() {
        // utility class
    }

    private static void configure() {

        try {

            Path logDirectory =
                    AppDirectories.getDataDirectory()
                            .resolve("logs");

            Files.createDirectories(logDirectory);

            FileHandler handler =
                    new FileHandler(
                            logDirectory.resolve(LOG_FILE).toString(),
                            true
                    );

            handler.setFormatter(new SimpleFormatter());

            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.ALL);
            LOGGER.addHandler(handler);

        } catch (IOException exception) {

            System.err.println("Cannot initialize logger");
            exception.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void warning(String message) {
        LOGGER.warning(message);
    }

    public static void error(
            String message,
            Exception exception
    ) {
        LOGGER.log(
                Level.SEVERE,
                message,
                exception
        );
    }
}