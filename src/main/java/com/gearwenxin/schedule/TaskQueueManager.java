package com.gearwenxin.schedule;

import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ImageResponse;
import com.gearwenxin.schedule.entity.BlockingMap;
import com.gearwenxin.schedule.entity.ChatTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Slf4j
@Getter
public class TaskQueueManager {

    private final BlockingMap<String, List<ChatTask>> taskMap = new BlockingMap<>();

    // 任务数量Map
    private final Map<String, Integer> taskCountMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> modelCurrentQPSMap = new ConcurrentHashMap<>();

    // 提交的任务Map
    private final BlockingMap<String, CompletableFuture<Flux<ChatResponse>>> chatFutureMap = new BlockingMap<>();
    private final BlockingMap<String, CompletableFuture<Mono<ImageResponse>>> imageFutureMap = new BlockingMap<>();

    private final Lock lock = new ReentrantLock();
    private final Map<String, CountDownLatch> latchMap = new ConcurrentHashMap<>();

    private volatile static TaskQueueManager instance = null;

    private TaskQueueManager() {
    }

    public static TaskQueueManager getInstance() {
        if (instance == null) {
            synchronized (TaskQueueManager.class) {
                if (instance == null) {
                    instance = new TaskQueueManager();
                }
            }
        }
        return instance;
    }

    public synchronized String addTask(ChatTask task) {
        String modelName = task.getModelConfig().getModelName();
        String taskId = UUID.randomUUID().toString();
        task.setTaskId(taskId);
        task.getModelConfig().setTaskId(taskId);
        log.info("add task for {}", modelName);

        Map<String, List<ChatTask>> innerMap = taskMap.getMap();
        List<ChatTask> chatTaskList = innerMap.get(modelName);
        if (chatTaskList == null) {
            List<ChatTask> list = new CopyOnWriteArrayList<>();
            list.add(task);
            taskMap.put(modelName, list);
            initTaskCount(modelName);
        } else {
            chatTaskList.add(task);
            taskMap.put(modelName, chatTaskList);
            upTaskCount(modelName);
        }
        log.info("task count: {}", getTaskCount(modelName));
        return taskId;
    }

    public ChatTask getTask(String modelName) {
        List<ChatTask> list = taskMap.get(modelName, true);
        downTaskCount(modelName);
        return list.remove(0);
    }

    public CompletableFuture<Flux<ChatResponse>> getChatFuture(String taskId) {
        return chatFutureMap.get(taskId);
    }

    public CompletableFuture<Mono<ImageResponse>> getImageFuture(String taskId) {
        return imageFutureMap.get(taskId);
    }

    public Set<String> getModelNames() {
        return taskMap.getMap().keySet();
    }

    public int getTaskCount(String modelName) {
        return taskCountMap.get(modelName);
    }

    public synchronized void initTaskCount(String modelName) {
        taskCountMap.put(modelName, 1);
        log.info("init task count for {}", modelName);
    }

    public synchronized void upTaskCount(String modelName) {
        Integer taskCount = taskCountMap.get(modelName);
        taskCountMap.put(modelName, taskCount + 1);
        log.info("up task count for {}, number {}", modelName, taskCount + 1);
    }

    public synchronized void downTaskCount(String modelName) {
        Integer taskCount = taskCountMap.get(modelName);
        if (taskCount == null || taskCount <= 0) {
            return;
        }
        taskCountMap.put(modelName, taskCount - 1);
        log.info("down task count for {}, number {}", modelName, taskCount - 1);
    }

}
