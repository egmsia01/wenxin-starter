package com.gearwenxin.schedule;

import com.gearwenxin.common.Constant;
import com.gearwenxin.common.RuntimeToolkit;
import com.gearwenxin.common.StatusConst;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import com.gearwenxin.schedule.entity.ChatTask;
import com.gearwenxin.service.ChatService;
import com.gearwenxin.service.ImageService;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.request.ImageBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.service.PromptService;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class TaskConsumerLoop {

    public static final String TAG = "TaskConsumerLoop";
    public static final int DEFAULT_QPS = -1;

    @Getter
    @Setter
    public CountDownLatch testCountDownLatch;

    @Getter
    @Setter
    private List<String> qpsList = null;

    @Resource
    private ChatService chatService;
    @Resource
    private PromptService promptService;
    @Resource
    private ImageService imageService;

    private static final Map<String, Integer> MODEL_QPS_MAP = new HashMap<>();

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    private final Map<String, CountDownLatch> countDownLatchMap = taskManager.getConsumerCountDownLatchMap();

    public void start() {
        initModelQPSMap();
        Set<String> modelNames = MODEL_QPS_MAP.keySet();
        modelNames.forEach(modelName -> new Thread(() -> {
            try {
                // 暂未使用
                if (!countDownLatchMap.containsKey(modelName)) {
                    countDownLatchMap.put(modelName, new CountDownLatch(1));
                }
                Thread.currentThread().setName(modelName + "-thread");
                log.info("[{}] {}, model: {}, loop start", TAG, Thread.currentThread().getName(), modelName);
                // 消费事件循环处理
                while (true) {
                    eventLoopProcess(modelName);
                }
            } catch (Exception e) {
                log.error("[{}] loop-process error, modelName: {}, thread-{}", TAG, modelName, Thread.currentThread().getName(), e);
                if (!Thread.currentThread().isAlive()) {
                    log.error("[{}] {} is not alive", TAG, Thread.currentThread().getName());
                }
            }
        }).start());
    }

    public void initModelQPSMap() {
        if (qpsList == null || qpsList.isEmpty()) {
            return;
        }
        log.debug("[{}] model qps list: {}", TAG, qpsList);
        // 用于检测消费线程是否启动
        qpsList.add(Constant.CHECK + " " + DEFAULT_QPS);
        qpsList.forEach(s -> {
            String[] split = s.split(" ");
            MODEL_QPS_MAP.put(split[0], Integer.parseInt(split[1]));
        });
        log.info("[{}] init model qps map complete", TAG);
    }

    private int getModelQPS(String modelName) {
        return MODEL_QPS_MAP.getOrDefault(modelName, DEFAULT_QPS);
    }

    /**
     * 消费事件循环处理
     */
    public void eventLoopProcess(String modelName) {
        Map<String, Integer> currentQPSMap = taskManager.getModelCurrentQPSMap();
        int modelQPS = getModelQPS(modelName);
        Integer currentQPS = currentQPSMap.computeIfAbsent(modelName, k -> {
            taskManager.initModelCurrentQPS(k);
            return 0;
        });
        log.debug("[{}] [{}] current qps: {}", TAG, modelName, currentQPS);
        if (currentQPS < modelQPS || modelQPS == DEFAULT_QPS) {
            ChatTask task = taskManager.getTask(modelName);
            Optional.ofNullable(task).ifPresentOrElse(t -> {
                log.debug("[{}] [{}] task: {}", TAG, modelName, t);
                submitTask(t);
                taskManager.upModelCurrentQPS(modelName);
            }, () -> sleep(1000));
        } else {
            // TODO: 待优化
//            RuntimeToolkit.threadWait(Thread.currentThread());
            sleep(1000);
        }

    }

    /**
     * 提交任务到不同的线程池
     */
    private void submitTask(ChatTask task) {
        String taskId = task.getTaskId();
        ModelConfig modelConfig = task.getModelConfig();
        // 根据不同的任务类型，获取不同的线程池实例
        ExecutorService executorService = ThreadPoolManager.getInstance(task.getTaskType());
        switch (task.getTaskType()) {
            case chat -> {
                var future = CompletableFuture.supplyAsync(() -> processChatTask(task, modelConfig), executorService);
                taskManager.getChatFutureMap().putAndNotify(taskId, future);
            }
            case prompt -> {
                var future = CompletableFuture.supplyAsync(() -> processPromptTask(task, modelConfig), executorService);
                taskManager.getPromptFutureMap().putAndNotify(taskId, future);
            }
            case image -> {
                var future = CompletableFuture.supplyAsync(() -> processImageTask(task, modelConfig), executorService);
                taskManager.getImageFutureMap().putAndNotify(taskId, future);
            }
            case embedding -> {
            }
            case check -> {
                // 用于检查消费线程是否启动
                StatusConst.SERVICE_STARTED = true;
                getTestCountDownLatch().countDown();
            }
            default -> log.error("[{}] unknown task type: {}", TAG, task.getTaskType());
        }
    }

    private Publisher<ChatResponse> processChatTask(ChatTask task, ModelConfig modelConfig) {
        // 如果包含"ernie"，则使用erni的请求类
        ChatBaseRequest taskRequest = modelConfig.getModelName().toLowerCase().contains("ernie") ?
                (ChatErnieRequest) task.getTaskRequest() : (ChatBaseRequest) task.getTaskRequest();
        log.debug("[{}] submit task {}, ernie: {}", TAG, task.getTaskId(), taskRequest.getClass() == ChatErnieRequest.class);
        return chatService.chatProcess(taskRequest, task.getMessageId(), task.isStream(), modelConfig);
    }

    private Mono<PromptResponse> processPromptTask(ChatTask task, ModelConfig modelConfig) {
        log.debug("[{}] submit task {}, type: prompt", TAG, task.getTaskId());
        return promptService.promptProcess((ChatPromptRequest) task.getTaskRequest(), modelConfig);
    }

    private Mono<ImageResponse> processImageTask(ChatTask task, ModelConfig modelConfig) {
        log.debug("[{}] submit task {}, type: image", TAG, task.getTaskId());
        return imageService.imageProcess((ImageBaseRequest) task.getTaskRequest(), modelConfig);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("[{}] thread sleep error", TAG);
            Thread.currentThread().interrupt();
        }
    }

}
