package com.gearwenxin.core;

import com.gearwenxin.schedule.ChatTask;
import com.gearwenxin.schedule.TaskQueueManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Order(2)
public class TaskHandler implements CommandLineRunner {

    @Value("${gear.wenxin.model.qps}")
    private List<String> modelQPSList;

    private static final Map<String, Integer> modelQPSMap = new HashMap<>();

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

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

    public void taskHandler(ChatTask task) {
        switch (task.getTaskType()) {
            case chat -> {
                int modelQPS = getModelQPS(task.getModelName());

            }
        }
    }
}
