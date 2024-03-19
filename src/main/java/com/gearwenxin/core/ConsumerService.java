package com.gearwenxin.core;

import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.schedule.TaskConsumerLoop;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Order(2)
@Component
public class ConsumerService implements CommandLineRunner {

    @Resource
    private TaskConsumerLoop taskConsumerLoop;

    @Resource
    private WenXinProperties wenXinProperties;

    @Override
    public void run(String... args) {
        // TODO: 曲线救国，初始化modelQPSList
        List<String> modelQPSList = wenXinProperties.getModelQPSList();
        taskConsumerLoop.setQpsList(modelQPSList);

        log.info("EventLoop start");
        taskConsumerLoop.start();
    }

}
