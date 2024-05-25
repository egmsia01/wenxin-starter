package com.gearwenxin.schedule;

import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.schedule.entity.BlockingMap;
import com.gearwenxin.schedule.entity.ChatTask;
import com.google.gson.Gson;
import javax.annotation.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class BackgroundSaveManager {

    @Resource
    private WenXinProperties wenXinProperties;

    private static final Gson gson = new Gson();

    private static final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    /**
     * 定时保存任务队列
     */
//    @Scheduled(fixedDelay = 2000)
//    public void saveTaskQueueThread() {
//        BlockingMap<String, List<ChatTask>> taskMap = taskManager.getTaskMap();
//        String taskMapJson = gson.toJson(taskMap);
////        SaveService.saveTaskQueue(taskMapJson);
//    }

}
