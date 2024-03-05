package com.gearwenxin.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
public class EventLoop implements CommandLineRunner {

    private static final TaskHandler taskHandler = TaskHandler.getInstance();

    @Override
    public void run(String... args) {
        log.info("EventLoop start");
        taskHandler.start();
    }

}
