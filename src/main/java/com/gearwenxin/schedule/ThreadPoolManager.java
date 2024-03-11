package com.gearwenxin.schedule;

import com.gearwenxin.entity.enums.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.gearwenxin.entity.enums.ModelType.addTask;

@Slf4j
public class ThreadPoolManager {

    public static final String TAG = "ThreadPoolManager";
    private static final int NUM_THREADS = 5;
    private static final int TASK_NUM_THREADS = 10;
    private static final ExecutorService[] executorServices = new ExecutorService[5];

    public static ExecutorService getInstance(ModelType type) {
        int index = getIndex(type);
        if (executorServices[index] == null) {
            synchronized (ExecutorService.class) {
                if (executorServices[index] == null) {
                    log.info("[{}] creat new thread pool for [{}]", TAG, type);
                    if (type == addTask) {
                        executorServices[index] = Executors.newFixedThreadPool(TASK_NUM_THREADS);
                    } else {
                        executorServices[index] = Executors.newFixedThreadPool(NUM_THREADS);
                    }
                }
            }
        }
        return executorServices[index];
    }

    private static int getIndex(ModelType type) {
        return switch (type) {
            case chat -> 0;
            case image -> 1;
            case prompt -> 2;
            case embedding -> 3;
            case addTask -> 4;
        };
    }

}
