package com.nolabke.utils;

import java.util.prefs.Preferences;

/**
 * Stores persistent application settings.
 */
public final class AppSettings {

    private static final Preferences preferences =
            Preferences.userNodeForPackage(AppSettings.class);

    private static final String FONT_SIZE_KEY = "fontSize";

    private static final int DEFAULT_FONT_SIZE = 16;

    private AppSettings() {

    }

    public static void setFontSizeKey(int size) {

        preferences.putInt(FONT_SIZE_KEY, size);
    }

    public static int getFontSizeKey() {
        return preferences.getInt(FONT_SIZE_KEY, DEFAULT_FONT_SIZE);
    }


}