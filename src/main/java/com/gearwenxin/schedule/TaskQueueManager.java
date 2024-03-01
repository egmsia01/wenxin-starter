package com.gearwenxin.schedule;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Slf4j
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

    public void addTask(ChatTask task) {
        String modelName = task.getModelName();
        Optional.ofNullable(taskMap.get(modelName)).ifPresentOrElse(list -> {
            list.add(task);
            upTaskCount(modelName);
        }, () -> {
            List<ChatTask> list = new CopyOnWriteArrayList<>();
            list.add(task);
            taskMap.put(modelName, list);
            initTaskCount(modelName);
        });
    }
    public ChatTask getTask(String modelName) {
        List<ChatTask> list = taskMap.get(modelName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        downTaskCount(modelName);
        return list.remove(0);
    }

    public Set<String> getModelNames() {
        return taskMap.keySet();
    }

    public int getTaskCount(String modelName) {
        return Optional.ofNullable(taskCountMap.get(modelName)).orElse(0);
    }

    private void initTaskCount(String modelName) {
        taskCountMap.put(modelName, 1);
        log.debug("init task count for {}", modelName);
    }

    private void upTaskCount(String modelName) {
        taskCountMap.put(modelName, taskCountMap.get(modelName) + 1);
        log.debug("up task count for {}, number {}", modelName, taskCountMap.get(modelName));
    }

    private void downTaskCount(String modelName) {
        Integer taskCount = taskCountMap.get(modelName);
        if (taskCount == null || taskCount <= 0) {
            return;
        }
        taskCountMap.put(modelName, taskCount - 1);
        log.debug("down task count for {}, number {}", modelName, taskCount - 1);
    }

    public static void main(String[] args) {
        TaskQueueManager instance = TaskQueueManager.getInstance();
        instance.addTask(new ChatTask("client1", "task1", 1.0f));
        instance.addTask(new ChatTask("client1", "task2", 1.0f));
        instance.addTask(new ChatTask("client2", "task1", 1.0f));
        instance.addTask(new ChatTask("client2", "task2", 1.0f));
        instance.addTask(new ChatTask("client2", "task2", 1.0f));
        instance.addTask(new ChatTask("client2", "task2", 1.0f));
        System.out.println(instance.getTaskCount("client2"));
        System.out.println(instance.getTask("client2"));

    }

}
