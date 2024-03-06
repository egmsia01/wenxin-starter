package com.gearwenxin.core;

import com.gearwenxin.client.ChatProcessor;
import com.gearwenxin.client.ImageProcessor;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.schedule.BlockingMap;
import com.gearwenxin.schedule.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.ThreadPoolManager;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
public class TaskHandler {
    public static final int DEFAULT_QPS = -1;

    @Getter
    @Setter
    private List<String> modelQPSList = null;

    @Resource
    private ChatProcessor chatProcessor;
    @Resource
    ImageProcessor imageProcessor;

    private static final Map<String, Integer> modelQPSMap = new HashMap<>();

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    private volatile static TaskHandler instance = null;

    public static TaskHandler getInstance() {
        if (instance == null) {
            synchronized (TaskHandler.class) {
                if (instance == null) {
                    instance = new TaskHandler();
                }
            }
        }
        return instance;
    }

    public void start() {
        initModelQPSMap();
        Set<String> modelNames = modelQPSMap.keySet();
        modelNames.forEach(modelName -> new Thread(() -> {
            try {
                log.info("thread-{}, loopProcess start", Thread.currentThread().getName());
                eventLoopProcess(modelName);
            } catch (Exception e) {
                log.error("eventLoopProcess error, modelName: {}, thread-{}", modelName, Thread.currentThread().getName(), e);
            }
        }).start());
    }

    public void initModelQPSMap() {
        if (modelQPSList == null || modelQPSList.isEmpty()) {
            return;
        }
        log.debug("modelQPSList: {}", modelQPSList);
        modelQPSList.forEach(s -> {
            String[] split = s.split(" ");
            modelQPSMap.put(split[0], Integer.parseInt(split[1]));
        });
        log.info("init model-qps-map complete");
    }

    private int getModelQPS(String modelName) {
        return modelQPSMap.getOrDefault(modelName, DEFAULT_QPS);
    }

    public void eventLoopProcess(String modelName) {
        Map<String, Integer> modelCurrentQPSMap = taskManager.getModelCurrentQPSMap();
        for (; ; ) {
            log.info("thread-{}, loopHandleTaskProcess, modelName: {}", Thread.currentThread().getName(), modelName);
            ChatTask task = taskManager.getTask(modelName);
            String taskId = task.getTaskId();
            log.info("get task: {}", task);
            Integer currentQPS = modelCurrentQPSMap.get(modelName);
            if (currentQPS == null) {
                currentQPS = 0;
                modelCurrentQPSMap.put(modelName, 0);
            }
            if (currentQPS <= getModelQPS(modelName) || getModelQPS(modelName) == DEFAULT_QPS) {
                modelCurrentQPSMap.put(modelName, currentQPS + 1);
                ExecutorService executorService = ThreadPoolManager.getInstance(task.getTaskType());
                switch (task.getTaskType()) {
                    case chat, embedding -> {
                        BlockingMap<String, CompletableFuture<Flux<ChatResponse>>> chatFutureMap = taskManager.getChatFutureMap();
                        // 提交任务到线程池
                        CompletableFuture<Flux<ChatResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
                            // 测试用，暂时这么写
                            ChatErnieRequest taskRequest = (ChatErnieRequest) task.getTaskRequest();
                            log.info("submit task {}", taskRequest.getContent());
                            return chatProcessor.chatSingleOfStream(taskRequest);
                        }, executorService);
                        chatFutureMap.put(taskId, completableFuture);
                        log.info("add a chat task, taskId: {}", taskId);
                        // TODO：不应该在这里，暂时放这里
                        modelCurrentQPSMap.put(taskId, modelCurrentQPSMap.get(modelName) - 1);
                    }
                    case image -> {
                        BlockingMap<String, CompletableFuture<Mono<ImageResponse>>> imageFutureMap = taskManager.getImageFutureMap();
                        CompletableFuture<Mono<ImageResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
                            ImageBaseRequest taskRequest = (ImageBaseRequest) task.getTaskRequest();
                            return imageProcessor.chatImage(taskRequest);
                        }, executorService);
                        imageFutureMap.put(taskId, completableFuture);
                        log.info("add a image task, taskId: {}", taskId);
                        // TODO：不应该在这里，暂时放这里
                        modelCurrentQPSMap.put(modelName, modelCurrentQPSMap.get(modelName) - 1);
                    }
                }
            } else {
                // 把任务再放回去
                // TODO: 待优化，QPS超额应该直接wait()
                taskManager.addTask(task);
            }
        }

    }

}
