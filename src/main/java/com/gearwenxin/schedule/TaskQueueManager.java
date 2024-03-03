package com.gearwenxin.schedule;

import com.gearwenxin.entity.enums.ModelType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Slf4j
@Getter
public class TaskQueueManager {

    private final Map<String, List<ChatTask>> taskMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> taskCountMap = new ConcurrentHashMap<>();

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

    public ChatTask getTask(String modelName) {
        List<ChatTask> list = taskMap.get(modelName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        downTaskCount(modelName);
        return list.remove(0);
    }

    public ChatTask getRandomTask() {
        List<ChatTask> list = null;
        String modelName = null;
        for (Map.Entry<String, List<ChatTask>> entry : taskMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                list = entry.getValue();
                modelName = entry.getKey();
                break;
            }
        }
        downTaskCount(modelName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.remove(0);
    }

    public Set<String> getModelNames() {
        return taskMap.keySet();
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

    public static void main(String[] args) {
        ExecutorService executor = ThreadPoolManager.getInstance(ModelType.chat);
        CompletableFuture<Flux<String>> future = CompletableFuture.supplyAsync(() -> {
            // 在线程池中执行任务，返回一个Flux
            return generateFluxData().subscribeOn(Schedulers.fromExecutor(executor));
        }, executor);

        // 处理CompletableFuture返回的Flux
        future.thenAccept(flux -> {
            flux.subscribe(System.out::println);
        });

        // 等待异步任务完成
        future.join();

        // 关闭线程池
        executor.shutdown();
    }

    // 模拟生成Flux数据的方法
    public static Flux<String> generateFluxData() {
        return Flux.just("Response 1", "Response 2", "Response 3");
    }


}
