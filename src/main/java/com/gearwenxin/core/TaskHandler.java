package com.gearwenxin.core;

import com.gearwenxin.client.ChatProcessor;
import com.gearwenxin.client.ImageProcessor;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.schedule.BlockingMap;
import com.gearwenxin.schedule.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.ThreadPoolManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
public class TaskHandler implements CommandLineRunner {

    private TaskHandler() {
    }

    @Value("${gear.wenxin.model.qps}")
    private List<String> modelQPSList;

    public static final int DEFAULT_QPS = -1;

    @Resource
    ChatProcessor chatProcessor;
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

    @Override
    public void run(String... args) {
        log.info("TaskHandler start");
        initModelQPSMap();
        loopHandleTaskProcess();
    }

    public void initModelQPSMap() {
        if (modelQPSList == null || modelQPSList.isEmpty()) {
            return;
        }
        modelQPSList.forEach(s -> {
            String[] split = s.split(" ");
            modelQPSMap.put(split[0], Integer.parseInt(split[1]));
        });
        log.info("init model-qps-map complete");
    }

    private int getModelQPS(String modelName) {
        return modelQPSMap.getOrDefault(modelName, DEFAULT_QPS);
    }

    public void loopHandleTaskProcess() {
        Map<String, Integer> modelCurrentQPSMap = taskManager.getModelCurrentQPSMap();
        for (; ; ) {
            log.info("loopHandleTaskProcess");
            ChatTask task = taskManager.getRandomTask();
            String taskId = task.getTaskId();
            String modelName = task.getModelName();
            log.info("task: {}", task);
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
                            return chatProcessor.chatSingleOfStream(taskRequest)
                                    .subscribeOn(Schedulers.fromExecutor(executorService));
                        }, executorService);
                        chatFutureMap.put(taskId, completableFuture);
                        // TODO：不应该在这里，暂时放这里
                        modelCurrentQPSMap.put(modelName, modelCurrentQPSMap.get(modelName) - 1);
                    }
                    case image -> {
                        BlockingMap<String, CompletableFuture<Mono<ImageResponse>>> imageFutureMap = taskManager.getImageFutureMap();
                        CompletableFuture<Mono<ImageResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
                            ImageBaseRequest taskRequest = (ImageBaseRequest) task.getTaskRequest();
                            return imageProcessor.chatImage(taskRequest)
                                    .subscribeOn(Schedulers.fromExecutor(executorService));
                        }, executorService);
                        imageFutureMap.put(taskId, completableFuture);
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
