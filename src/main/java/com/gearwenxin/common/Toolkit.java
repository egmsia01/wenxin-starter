package com.gearwenxin.common;

public class Toolkit {

    public static void ifOrElse(boolean condition, Runnable ifRunnable, Runnable elseRunnable) {
        if (condition) {
            ifRunnable.run();
        } else {
            elseRunnable.run();
        }

    }

}
