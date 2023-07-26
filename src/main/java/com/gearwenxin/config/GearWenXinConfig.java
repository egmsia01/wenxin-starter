package com.gearwenxin.config;

import com.gearwenxin.client.Bloomz7BClient;
import com.gearwenxin.client.ErnieBotClient;
import com.gearwenxin.client.ErnieBotTurboClient;
import com.gearwenxin.client.PromptBotClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ge Mingjia
 */
@Data
@Configuration
@ComponentScan
@ConfigurationProperties("gear.wenxin")
public class GearWenXinConfig {

    private String accessToken;

    @Bean
    public ErnieBotClient ernieBotClient() {
        return new ErnieBotClient(accessToken);
    }

    @Bean
    public ErnieBotTurboClient ernieBotTurboClient() {
        return new ErnieBotTurboClient(accessToken);
    }

    @Bean
    public Bloomz7BClient bloomz7BClient() {
        return new Bloomz7BClient(accessToken);
    }

    @Bean
    public PromptBotClient promptBotClient() {
        return new PromptBotClient(accessToken);
    }

}
