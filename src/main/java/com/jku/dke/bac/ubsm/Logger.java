package com.jku.dke.bac.ubsm;

import java.time.LocalDateTime;

public class Logger {
    private static boolean isOn = true;

    public static void log(String s) {
        if (isOn) System.out.println(LocalDateTime.now() + s);
    }
}

