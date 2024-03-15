package com.gearwenxin.config;

import com.gearwenxin.core.WebManager;
import com.gearwenxin.schedule.TaskConsumerLoop;
import com.gearwenxin.entity.response.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Optional;

/**
 * @author Ge Mingjia
 */
@Slf4j
@Order(1)
@AutoConfiguration
@EnableConfigurationProperties(WenXinProperties.class)
public class GearWenXinConfig implements CommandLineRunner {

    private final WenXinProperties wenXinProperties;

    private final TaskConsumerLoop taskConsumerLoop;

    @Autowired
    public GearWenXinConfig(WenXinProperties wenXinProperties, TaskConsumerLoop taskConsumerLoop) {
        this.wenXinProperties = wenXinProperties;
        this.taskConsumerLoop = taskConsumerLoop;
    }

    @Override
    public void run(String... args) {
        String apiKey = wenXinProperties.getApiKey();
        String secretKey = wenXinProperties.getSecretKey();
        String accessToken = wenXinProperties.getAccessToken();

        // TODO: 曲线救国，初始化modelQPSList
        List<String> modelQPSList = wenXinProperties.getModelQPSList();
        taskConsumerLoop.setQpsList(modelQPSList);

        if (apiKey == null && secretKey == null) {
            return;
        }
        if (accessToken != null) {
            log.info("[global] access-token: {}", accessToken);
            return;
        }
        try {
            WebManager.getAccessTokenByAKSK(apiKey, secretKey).doOnNext(tokenResponse -> {
                if (tokenResponse != null) {
                    Optional.ofNullable(tokenResponse.getAccessToken()).ifPresentOrElse(token -> {
                        wenXinProperties.setAccessToken(token);
                        log.info("[global] access-token: {}", token);
                    }, () -> log.error("""
                             api-key or secret-key error！
                             error_description: {}
                             error: {}
                            """, tokenResponse.getErrorDescription(), tokenResponse.getError()));
                }
            }).map(TokenResponse::getAccessToken).block();
        } catch (Exception e) {
            log.error("get access-token error, {}", e.getMessage());
        }
    }

}
