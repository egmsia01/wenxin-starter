package com.gearwenxin.schedule;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
@Slf4j
public class TaskQueueManager {

    Map<String, List<ChatTask>> taskMap = new ConcurrentHashMap<>();
    Map<String, Integer> taskCountMap = new ConcurrentHashMap<>();

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
        String clientName = task.getModelName();
        Optional.ofNullable(taskMap.get(clientName)).ifPresentOrElse(list -> {
            list.add(task);
            upTaskCount(clientName);
        }, () -> {
            List<ChatTask> list = new CopyOnWriteArrayList<>();
            list.add(task);
            taskMap.put(clientName, list);
            initTaskCount(clientName);
        });
    }

    public ChatTask getTask(String clientName) {
        List<ChatTask> list = taskMap.get(clientName);
        if (list == null || list.isEmpty()) {
            return null;
        }
        downTaskCount(clientName);
        return list.remove(0);
    }

    private void initTaskCount(String clientName) {
        taskCountMap.put(clientName, 0);
        log.debug("init task count for {}", clientName);
    }

    private void upTaskCount(String clientName) {
        taskCountMap.put(clientName, taskCountMap.get(clientName) + 1);
        log.debug("up task count for {}, number {}", clientName, taskCountMap.get(clientName));
    }

    private void downTaskCount(String clientName) {
        Integer taskCount = taskCountMap.get(clientName);
        if (taskCount == null || taskCount <= 0) {
            return;
        }
        taskCountMap.put(clientName, taskCount - 1);
        log.debug("down task count for {}, number {}", clientName, taskCount - 1);
    }

}
