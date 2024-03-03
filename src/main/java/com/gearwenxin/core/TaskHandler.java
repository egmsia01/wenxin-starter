package com.gearwenxin.core;

import com.gearwenxin.schedule.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

@Slf4j
@Order(2)
public class TaskHandler implements CommandLineRunner {

    private TaskHandler() {
    }

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

    @Value("${gear.wenxin.model.qps}")
    private List<String> modelQPSList;

    private static final Map<String, Integer> modelQPSMap = new HashMap<>();

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    private final Map<String, List<ChatTask>> taskMap = taskManager.getTaskMap();

    @Override
    public void run(String... args) {
        initModelQPSMap();
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
        return modelQPSMap.getOrDefault(modelName, -1);
    }

    public void addTask(ChatTask task) {
        String modelName = task.getModelName();
        Optional.ofNullable(taskMap.get(modelName)).ifPresentOrElse(list -> {
            list.add(task);
            taskManager.upTaskCount(modelName);
        }, () -> {
            List<ChatTask> list = new CopyOnWriteArrayList<>();
            list.add(task);
            taskMap.put(modelName, list);
            taskManager.initTaskCount(modelName);
        });
        // 提交任务到线程池
        Future<?> future = ThreadPoolManager.getInstance(task.getTaskType()).submit(() -> {
            log.debug("submit task for {}, task count is {}", modelName, taskManager.getTaskCount(modelName));
        });
    }

}
