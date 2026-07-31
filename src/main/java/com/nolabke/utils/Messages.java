package com.nolabke.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {

    private static final String BUNDLE_NAME = "messages";
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static volatile Locale currentLocale = Locale.getDefault();
    private static volatile ResourceBundle bundle =
            loadBundle(currentLocale);

    private Messages() {
    }

    private static ResourceBundle loadBundle(Locale locale) {

        try {
            return ResourceBundle.getBundle(
                    BUNDLE_NAME,
                    locale
            );

        } catch (MissingResourceException e) {

            AppLogger.warning(
                    "Cannot load resource bundle for locale: "
                            + currentLocale
                            + ". Falling back to default locale: "
                            + DEFAULT_LOCALE
            );

            currentLocale = DEFAULT_LOCALE;

            return ResourceBundle.getBundle(
                    BUNDLE_NAME,
                    DEFAULT_LOCALE
            );
        }
    }


    public static String get(String key) {

        try {
            return bundle.getString(key);

        } catch (MissingResourceException e) {
            AppLogger.warning(
                    "Missing translation key: " + key
            );

            return key;
        }
    }


    public static void setLanguage(String languageCode) {

        Locale locale = Locale.forLanguageTag(languageCode);

        bundle = loadBundle(locale);
        currentLocale = locale;
    }


    public static Locale getCurrentLocale() {
        return currentLocale;
    }
}