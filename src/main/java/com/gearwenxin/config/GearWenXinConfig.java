package com.gearwenxin.config;

import com.gearwenxin.core.ChatCore;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.core.TaskHandler;
import com.gearwenxin.entity.response.TokenResponse;
import com.gearwenxin.exception.WenXinException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Objects;
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

    private final TaskHandler taskHandler;

    @Autowired
    public GearWenXinConfig(WenXinProperties wenXinProperties, TaskHandler taskHandler) {
        this.wenXinProperties = wenXinProperties;
        this.taskHandler = taskHandler;
    }

    @Override
    public void run(String... args) {
        String apiKey = wenXinProperties.getApiKey();
        String secretKey = wenXinProperties.getSecretKey();
        String accessToken = wenXinProperties.getAccessToken();

        // TODO: 曲线救国，初始化modelQPSList
        List<String> modelQPSList = wenXinProperties.getModelQPSList();
        taskHandler.setModelQPSList(modelQPSList);

        if (apiKey == null || secretKey == null) {
            return;
        }
        ChatCore.getAccessTokenByAKSK(apiKey, secretKey)
                .filter(Objects::nonNull)
                .doOnNext(tokenResponse -> Optional.ofNullable(tokenResponse.getAccessToken())
                        .ifPresentOrElse(token -> {
                            wenXinProperties.setAccessToken(token);
                            log.info("accessToken => {}", token);
                        }, () -> {
                            if (accessToken == null) {
                                throw new WenXinException(ErrorCode.SYSTEM_ERROR, "api_key or secret_key error！");
                            }
                        }))
                .map(TokenResponse::getAccessToken)
                .block();
        // 再次检测wenXinProperties是否被正确赋值
        Optional.ofNullable(wenXinProperties.getAccessToken())
                .orElseThrow(() -> new WenXinException(ErrorCode.SYSTEM_ERROR, "accessToken 未被正确赋值！"));
    }

}
