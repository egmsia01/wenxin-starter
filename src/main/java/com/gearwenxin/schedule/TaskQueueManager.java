package com.gearwenxin.schedule;

import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ImageResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
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
    private final Condition keyPresent = lock.newCondition();

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

    public String addTask(ChatTask task) {
        lock.lock();
        try {
            String modelName = task.getModelName();
            String taskId = UUID.randomUUID().toString();
            task.setTaskId(taskId);
            log.info("add task for {}", modelName);
            Optional.ofNullable(taskMap.getMap().get(modelName))
                    .ifPresentOrElse(list -> {
                        list.add(task);
                        taskMap.put(modelName, list);
                        upTaskCount(modelName);
                    }, () -> {
                        List<ChatTask> list = new CopyOnWriteArrayList<>();
                        list.add(task);
                        taskMap.put(modelName, list);
                        initTaskCount(modelName);
                    });
            keyPresent.signalAll();
            return taskId;
        } finally {
            lock.unlock();
        }
    }

    public ChatTask getTask(String modelName) {
        lock.lock();
        List<ChatTask> list = taskMap.get(modelName);
        while (list == null || list.isEmpty()) {
            try {
                keyPresent.await();
            } catch (InterruptedException e) {
                log.error("get task error", e);
            } finally {
                lock.unlock();
            }
        }
        downTaskCount(modelName);
        return list.remove(0);
    }

    public ChatTask getRandomTask() {
        lock.lock();
        try {
            while (true) {
                List<Map.Entry<String, List<ChatTask>>> nonEmptyLists = taskMap.getMap().entrySet().stream()
                        .filter(entry -> !entry.getValue().isEmpty())
                        .toList();

                if (!nonEmptyLists.isEmpty()) {
                    int randomIndex = ThreadLocalRandom.current().nextInt(nonEmptyLists.size());
                    Map.Entry<String, List<ChatTask>> randomEntry = nonEmptyLists.get(randomIndex);
                    String modelName = randomEntry.getKey();
                    List<ChatTask> list = randomEntry.getValue();

                    downTaskCount(modelName);
                    return list.remove(0);
                } else {
                    try {
                        keyPresent.await();
                    } catch (InterruptedException e) {
                        log.error("get random task error", e);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
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
        return Optional.ofNullable(taskCountMap.get(modelName)).orElse(0);
    }

    public void initTaskCount(String modelName) {
        taskCountMap.put(modelName, 1);
        log.debug("init task count for {}", modelName);
    }

    public void upTaskCount(String modelName) {
        taskCountMap.put(modelName, taskCountMap.get(modelName) + 1);
        log.debug("up task count for {}, number {}", modelName, taskCountMap.get(modelName));
    }

    public void downTaskCount(String modelName) {
        Integer taskCount = taskCountMap.get(modelName);
        if (taskCount == null || taskCount <= 0) {
            return;
        }
        taskCountMap.put(modelName, taskCount - 1);
        log.debug("down task count for {}, number {}", modelName, taskCount - 1);
    }

}
