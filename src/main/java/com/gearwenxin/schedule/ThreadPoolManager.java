package com.gearwenxin.schedule;

import com.gearwenxin.entity.enums.ModelType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.gearwenxin.entity.enums.ModelType.addTask;
import static com.gearwenxin.entity.enums.ModelType.check;

@Slf4j
public class ThreadPoolManager {

    public static final String TAG = "ThreadPoolManager";
    private static final int NUM_THREADS = 5;
    private static final int TASK_NUM_THREADS = 10;
    private static final ExecutorService[] executorServices = new ExecutorService[6];

    public static ExecutorService getInstance(ModelType type) {
        int index = getIndex(type);
        if (executorServices[index] == null) {
            synchronized (ExecutorService.class) {
                if (executorServices[index] == null) {
                    log.info("[{}] creat new thread pool for [{}]", TAG, type);
                    if (type == check) {
                        executorServices[index] = Executors.newFixedThreadPool(1);
                    } else {
                        executorServices[index] = Executors.newFixedThreadPool(NUM_THREADS);
                    }
                }
            }
        }
        return executorServices[index];
    }

    private static int getIndex(ModelType type) {
        switch (type) {
            case chat:
                return 0;
            case image:
                return 1;
            case prompt:
                return 2;
            case embedding:
                return 3;
            case addTask:
                return 4;
            case check:
                return 5;
        }
        return 0;
    }

}
