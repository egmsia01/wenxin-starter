package com.gearwenxin.core;

import com.gearwenxin.schedule.TaskConsumerLoop;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class ConsumerService implements CommandLineRunner {

    @Resource
    private TaskConsumerLoop taskConsumerLoop;

    @Override
    public void run(String... args) {
        log.info("EventLoop start");
        taskConsumerLoop.start();
    }

}
