package com.gearwenxin.core;

import com.gearwenxin.common.Constant;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.StatusConst;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.enums.ModelType;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.schedule.TaskConsumerLoop;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.entity.ChatTask;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Order(3)
@Component
public class ThreadStartCheckService implements CommandLineRunner {

    private static final TaskQueueManager taskQueueManager = TaskQueueManager.getInstance();

    @Resource
    private TaskConsumerLoop taskConsumerLoop;

    @Override
    public void run(String... args) {
        ChatTask checkTask = ChatTask.builder().
                taskType(ModelType.check)
                .modelConfig(ModelConfig.builder().modelName(Constant.CHECK).build())
                .build();
        taskQueueManager.addTask(checkTask);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        taskConsumerLoop.setTestCountDownLatch(countDownLatch);
        try {
            log.info("Waiting for consumer thread to start...");
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new WenXinException(ErrorCode.CONSUMER_THREAD_START_FAILED);
        }
        if (StatusConst.SERVICE_STARTED) {
            log.info("Consumer thread started.");
        } else {
            throw new WenXinException(ErrorCode.CONSUMER_THREAD_START_FAILED);
        }
    }

}