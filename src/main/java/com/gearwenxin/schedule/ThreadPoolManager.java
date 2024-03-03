package com.gearwenxin.schedule;

import com.gearwenxin.entity.enums.ModelType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private static final int NUM_THREADS = 5;
    private static final ExecutorService[] executorServices = new ExecutorService[3];

    public static ExecutorService getInstance(ModelType type) {
        int index = getIndex(type);
        if (executorServices[index] == null) {
            synchronized (ExecutorService.class) {
                if (executorServices[index] == null) {
                    executorServices[index] = Executors.newFixedThreadPool(NUM_THREADS);
                }
            }
        }
        return executorServices[index];
    }

    private static int getIndex(ModelType type) {
        return switch (type) {
            case chat -> 0;
            case image -> 1;
            case embedding -> 2;
        };
    }

}
