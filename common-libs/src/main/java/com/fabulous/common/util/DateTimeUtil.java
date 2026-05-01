package com.fabulous.common.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : null;
    }

    public static LocalDateTime parse(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER);
        } catch (Exception e) {
            log.error("Error parsing date time: {}", dateTimeString, e);
            return null;
        }
    }

    public static boolean isAfter(LocalDateTime date1, LocalDateTime date2) {
        return date1 != null && date2 != null && date1.isAfter(date2);
    }

    public static boolean isBefore(LocalDateTime date1, LocalDateTime date2) {
        return date1 != null && date2 != null && date1.isBefore(date2);
    }
}