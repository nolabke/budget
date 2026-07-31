package com.nolabke.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DateUtils {

    private DateUtils() {
    }

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String formatDate(LocalDate date) {
        Objects.requireNonNull(date, "date cannot be null");
        return date.format(DATE_FORMATTER);
    }
    public static LocalDate parse(String text) {
        return LocalDate.parse(text, DATE_FORMATTER);
    }
}