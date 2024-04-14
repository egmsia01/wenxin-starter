package com.gearwenxin.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuntimeToolkit {

    public static void ifOrElse(boolean condition, Runnable ifRunnable, Runnable elseRunnable) {
        if (condition) {
            ifRunnable.run();
        } else {
            elseRunnable.run();
        }
    }

    public static void threadWait(Thread thread) {
        try {
            thread.wait();
        } catch (InterruptedException e) {
            log.error("[{}] wait error", thread.getName(), e);
        }
    }

    public static void threadNotify(Thread thread) {
        try {
            thread.notify();
        } catch (Exception e) {
            log.error("[{}] notify error", thread.getName(), e);
        }
    }

}
