package com.gearwenxin.core;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class TaskLoop implements CommandLineRunner {

    @Resource
    private TaskHandler taskHandler;

    @Override
    public void run(String... args) {
        log.info("EventLoop start");
        taskHandler.start();
    }

}
