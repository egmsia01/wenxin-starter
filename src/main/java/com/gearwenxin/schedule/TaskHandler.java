package com.gearwenxin.schedule;

import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.schedule.entity.BlockingMap;
import com.gearwenxin.schedule.entity.ChatTask;
import com.gearwenxin.service.ChatService;
import com.gearwenxin.service.ImageService;
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

    public static final String TAG = "TaskHandler";
    public static final int DEFAULT_QPS = -1;

    @Getter
    @Setter
    private List<String> modelQPSList = null;

    @Resource
    private ChatService chatService;
    @Resource
    private ImageService imageService;

    private static final Map<String, Integer> modelQPSMap = new HashMap<>();

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    public void start() {
        initModelQPSMap();
        Set<String> modelNames = modelQPSMap.keySet();
        modelNames.forEach(modelName -> new Thread(() -> {
            try {
                Thread.currentThread().setName(modelName + "-thread");
                log.info("[{}] {}, model: {}, loop start", TAG, Thread.currentThread().getName(), modelName);
                eventLoopProcess(modelName);
            } catch (Exception e) {
                log.error("[{}] loop-process error, modelName: {}, thread-{}", TAG, modelName, Thread.currentThread().getName(), e);
                if (!Thread.currentThread().isAlive()) {
                    log.error("[{}] {} is not alive", TAG, Thread.currentThread().getName());
                    log.info("[{}] restarting model: {}", TAG, modelName);
                    Thread.currentThread().start();
                }
            }
        }).start());
    }

    public void initModelQPSMap() {
        if (modelQPSList == null || modelQPSList.isEmpty()) {
            return;
        }
        log.debug("[{}] model qps list: {}", TAG, modelQPSList);
        modelQPSList.forEach(s -> {
            String[] split = s.split(" ");
            modelQPSMap.put(split[0], Integer.parseInt(split[1]));
        });
        log.info("[{}] init model qps map complete", TAG);
    }

    private int getModelQPS(String modelName) {
        return modelQPSMap.getOrDefault(modelName, DEFAULT_QPS);
    }

    public void eventLoopProcess(String modelName) {
        Map<String, Integer> modelCurrentQPSMap = taskManager.getModelCurrentQPSMap();
        for (; ; ) {
            Integer currentQPS = modelCurrentQPSMap.get(modelName);
            if (currentQPS == null) {
                taskManager.initModelCurrentQPS(modelName);
                currentQPS = 0;
            }
            log.debug("[{}] [{}] current qps: {}", TAG, modelName, currentQPS);
            if (currentQPS < getModelQPS(modelName) || getModelQPS(modelName) == DEFAULT_QPS) {
                ChatTask task = taskManager.getTask(modelName);
                if (task == null) {
                    try {
                        log.debug("[{}] model: {}, sleep 1s", TAG, modelName);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("[{}] thread sleep error", TAG, e);
                    }
                    continue;
                }
                String taskId = task.getTaskId();
                ModelConfig modelConfig = task.getModelConfig();
                log.debug("[{}] get task: {}", TAG, task);
                taskManager.upModelCurrentQPS(modelName);
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
                            log.debug("[{}] submit task {}, ernie: {}", TAG, taskId, taskRequest.getClass() == ChatErnieRequest.class);
                            if (task.getMessageId() != null) {
                                return chatService.chatContinuousStream(taskRequest, task.getMessageId(), modelConfig);
                            } else {
                                return chatService.chatOnceStream(taskRequest, modelConfig);
                            }
                        }, executorService);
                        chatFutureMap.put(taskId, completableFuture);
                    }
                    case image -> {
                        BlockingMap<String, CompletableFuture<Mono<ImageResponse>>> imageFutureMap = taskManager.getImageFutureMap();
                        CompletableFuture<Mono<ImageResponse>> completableFuture = CompletableFuture.supplyAsync(() -> {
                            ImageBaseRequest taskRequest = (ImageBaseRequest) task.getTaskRequest();
                            return imageService.chatImage(taskRequest);
                        }, executorService);
                        imageFutureMap.put(taskId, completableFuture);
                        log.debug("[{}] add a image task, taskId: {}", TAG, taskId);
                    }
                }
            } else {
                // TODO: 待优化，QPS超额应该直接wait()
                try {
                    log.debug("[{}] model: {}, sleep 1s", TAG, modelName);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("[{}] thread sleep error", TAG, e);
                }
            }
        }

    }

}
