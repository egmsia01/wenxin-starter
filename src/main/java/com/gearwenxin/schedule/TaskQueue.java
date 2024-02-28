package com.gearwenxin.schedule;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author GMerge
 * {@code @date} 2024/2/28
 */
public class TaskQueue {

    List<ChatTask> taskQueue = new CopyOnWriteArrayList<>();

    private volatile static TaskQueue instance = null;

    private TaskQueue() {
    }

    public static TaskQueue getInstance() {
        if (instance == null) {
            synchronized (TaskQueue.class) {
                if (instance == null) {
                    instance = new TaskQueue();
                }
            }
        }
        return instance;
    }

    public void addTask(ChatTask task) {
        taskQueue.add(task);
    }

    public ChatTask getTask() {
        if (taskQueue.isEmpty()) {
            return null;
        }
        return taskQueue.remove(0);
    }

}
