package com.nolabke.utils;

import java.math.BigDecimal;

public class NumberParser {

    public static BigDecimal parse(String input) {

        if (input == null || input.isBlank()) {
            throw new NumberFormatException("Empty number");
        }

        String value = input.trim();

        // usuwamy spacje zwykłe i nierozdzielające
        value = value.replace(" ", "")
                .replace("\u00A0", "");

        // znajdź ostatni separator
        int lastDot = value.lastIndexOf('.');
        int lastComma = value.lastIndexOf(',');

        int decimalSeparator = Math.max(lastDot, lastComma);

        if (decimalSeparator >= 0) {

            String integerPart =
                    value.substring(0, decimalSeparator)
                            .replace(".", "")
                            .replace(",", "");

            String decimalPart =
                    value.substring(decimalSeparator + 1)
                            .replace(".", "")
                            .replace(",", "");

            value = integerPart + "." + decimalPart;

        } else {

            value = value.replace(".", "")
                    .replace(",", "");
        }

        return new BigDecimal(value);
    }
}