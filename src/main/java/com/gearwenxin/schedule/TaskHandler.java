package com.gearwenxin.schedule;

import com.gearwenxin.common.ModelConfig;
import com.gearwenxin.service.ChatService;
import com.gearwenxin.client.ImageProcessor;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ImageResponse;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class TaskHandler {
    public static final int DEFAULT_QPS = -1;

    @Getter
    @Setter
    private List<String> modelQPSList = null;

    @Resource
    private ChatService chatService;
    @Resource
    private ImageProcessor imageProcessor;

    private static final Map<String, Integer> modelQPSMap = new HashMap<>();

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    public void start() {
        initModelQPSMap();
        Set<String> modelNames = modelQPSMap.keySet();
        modelNames.forEach(modelName -> new Thread(() -> {
            try {
                log.info("thread-{}, model: {}, loop-process start", Thread.currentThread().getName(), modelName);
                eventLoopProcess(modelName);
            } catch (Exception e) {
                log.error("loop-process error, modelName: {}, thread-{}", modelName, Thread.currentThread().getName(), e);
            }
        }).start());
    }

    public void initModelQPSMap() {
        if (modelQPSList == null || modelQPSList.isEmpty()) {
            return;
        }
        log.debug("model qps list: {}", modelQPSList);
        modelQPSList.forEach(s -> {
            String[] split = s.split(" ");
            modelQPSMap.put(split[0], Integer.parseInt(split[1]));
        });
        log.info("init model qps map complete");
    }

    private int getModelQPS(String modelName) {
        return modelQPSMap.getOrDefault(modelName, DEFAULT_QPS);
    }

    public void eventLoopProcess(String modelName) {
        Map<String, Integer> modelCurrentQPSMap = taskManager.getModelCurrentQPSMap();
        for (; ; ) {
            ChatTask task = taskManager.getTask(modelName);
            String taskId = task.getTaskId();
            ModelConfig modelConfig = task.getModelConfig();
            log.debug("get task: {}", task);
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
                            // 如果包含ernie，则使用erni的请求类
                            ChatBaseRequest taskRequest;
                            if (modelConfig.getModelName().toLowerCase().contains("ernie")) {
                                taskRequest = (ChatErnieRequest) task.getTaskRequest();
                            } else {
                                taskRequest = (ChatBaseRequest) task.getTaskRequest();
                            }
                            log.info("submit task {}, ernie: {}", taskRequest.getContent(), taskRequest.getClass() == ChatErnieRequest.class);
                            if (task.getMessageId() != null) {
                                return chatService.chatContinuousStream(taskRequest, task.getMessageId(), modelConfig);
                            } else return chatService.chatOnceStream(taskRequest, modelConfig);
                        }, executorService);
                        chatFutureMap.put(taskId, completableFuture);
                        log.info("add a chat task, taskId: {}", taskId);
                    }
                    case image -> {
                        BlockingMap<String, CompletableFuture<Mono<ImageResponse>>> imageFutureMap = taskManager.getImageFutureMap();
                        CompletableFuture<Mono<ImageResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
                            ImageBaseRequest taskRequest = (ImageBaseRequest) task.getTaskRequest();
                            return imageProcessor.chatImage(taskRequest);
                        }, executorService);
                        imageFutureMap.put(taskId, completableFuture);
                        log.debug("add a image task, taskId: {}", taskId);
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
