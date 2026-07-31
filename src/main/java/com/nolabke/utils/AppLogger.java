package com.nolabke.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public final class AppLogger {

    private static final Logger LOGGER =
            Logger.getLogger("NoLabke");

    private static final String LOG_FOLDER = "logs";
    private static final String LOG_FILE = "nolabke.log";


    static {

        configure();
    }


    private AppLogger() {
        // utility class
    }


    private static void configure() {

        try {

            File folder =
                    new File(LOG_FOLDER);

            if (!folder.exists()) {
                folder.mkdirs();
            }


            FileHandler handler =
                    new FileHandler(
                            LOG_FOLDER + File.separator + LOG_FILE,
                            true
                    );


            handler.setFormatter(
                    new SimpleFormatter()
            );


            LOGGER.addHandler(handler);

            LOGGER.setUseParentHandlers(false);


            LOGGER.setLevel(
                    Level.ALL
            );


        } catch (IOException e) {

            System.err.println(
                    "Cannot initialize logger"
            );

            e.printStackTrace();
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