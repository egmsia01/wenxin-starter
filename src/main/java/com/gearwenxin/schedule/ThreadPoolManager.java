package com.gearwenxin.schedule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private static final int NUM_THREADS = 5;
    private static volatile ExecutorService executor;

    public static ExecutorService getInstance() {
        if (executor == null) {
            synchronized (ThreadPoolManager.class) {
                if (executor == null || executor.isShutdown()) {
                    executor = Executors.newFixedThreadPool(NUM_THREADS);
                }
            }
        }
        return executor;
    }

}
