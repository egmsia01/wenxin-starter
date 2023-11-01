package com.gearwenxin.config;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.entity.response.TokenResponse;
import com.gearwenxin.exception.WenXinException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Objects;

/**
 * @author Ge Mingjia
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(WenXinProperties.class)
public class GearWenXinConfig implements CommandLineRunner {

    private final WenXinProperties wenXinProperties;

    @Autowired
    public GearWenXinConfig(WenXinProperties wenXinProperties) {
        this.wenXinProperties = wenXinProperties;
    }

    @Override
    public void run(String... args) {
        String apiKey = wenXinProperties.getApiKey();
        String secretKey = wenXinProperties.getSecretKey();
        String accessToken = wenXinProperties.getAccessToken();
        if (apiKey == null || secretKey == null) {
            return;
        }
        ChatUtils.getAccessTokenByAKSK(apiKey, secretKey)
                .filter(Objects::nonNull)
                .doOnNext(tokenResponse -> {
                    if (tokenResponse.getAccessToken() == null && accessToken == null) {
                        throw new WenXinException(ErrorCode.SYSTEM_ERROR, "api_key or secret_key error！");
                    }
                })
                .map(TokenResponse::getAccessToken)
                .doOnNext(wenXinProperties::setAccessToken)
                .subscribe();
    }

}
