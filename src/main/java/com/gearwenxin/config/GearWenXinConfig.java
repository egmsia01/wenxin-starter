package com.gearwenxin.config;

import com.gearwenxin.core.MessageHistoryManager;
import com.gearwenxin.core.WebManager;
import com.gearwenxin.entity.Message;
import com.gearwenxin.schedule.TaskConsumerLoop;
import com.gearwenxin.entity.response.TokenResponse;
import com.gearwenxin.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.*;

/**
 * @author Ge Mingjia
 */
@Slf4j
@Order(1)
@AutoConfiguration
@ConditionalOnClass(MessageService.class)
@EnableConfigurationProperties(value = {WenXinProperties.class})
public class GearWenXinConfig implements CommandLineRunner {

    @Resource
    private WenXinProperties wenXinProperties;

    private static final MessageHistoryManager messageHistoryManager = MessageHistoryManager.getInstance();

    @Override
    public void run(String... args) {
        String apiKey = wenXinProperties.getApiKey();
        String secretKey = wenXinProperties.getSecretKey();
        String accessToken = wenXinProperties.getAccessToken();

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

    @Bean
    @ConditionalOnMissingBean
    public MessageService defaultMessageService() {
        log.warn("[default] message service, It is recommended to provide developer autonomy");
        return new MessageService() {
            @Override
            public Deque<Message> getHistoryMessages(String id) {
//                Map<String, Deque<Message>> messageMap = messageHistoryManager.getChatMessageHistoryMap();
//                return messageMap.computeIfAbsent(id, k -> new ArrayDeque<>());
                return null;
            }

            @Override
            public void addHistoryMessage(String id, Message message) {
//                Deque<Message> historyMessages = getHistoryMessages(id);
//                MessageHistoryManager.addMessage(historyMessages, message);
            }

            @Override
            public void addHistoryMessage(Deque<Message> historyMessages, Message message) {
//                MessageHistoryManager.addMessage(historyMessages, message);
            }
        };
    }

}
