package com.jku.dke.bac.ubsm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static boolean isOn = true;

    public static void log(String s) {
        if (isOn) System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " - " + s);
    }
}

