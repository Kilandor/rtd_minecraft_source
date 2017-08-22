package com.czecherface.adminpowers;

import java.util.TimerTask;

public class TimerHandler extends TimerTask {

    private static TimerHandler instance;

    public static TimerHandler getInstance() {
        if (instance == null) {
            instance = new TimerHandler();
        }
        return instance;
    }

    private TimerHandler() {
    }

    public void run() {
    }
}
