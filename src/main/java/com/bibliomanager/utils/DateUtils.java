package com.bibliomanager.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getFormattedDate() {
        LocalDate now = java.time.LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
        String date = now.format(formatter);
        return date.substring(0, 1).toUpperCase() + date.substring(1);
    }

}
