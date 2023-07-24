package com.gearwenxin.config;

import com.gearwenxin.client.erniebot.ErnieBot;
import com.gearwenxin.client.erniebot.ErnieBotClient;
import com.gearwenxin.client.erniebot.ErnieBotTurboClient;
import com.gearwenxin.model.erniebot.ChatErnieRequest;
import com.gearwenxin.model.erniebot.ChatTurboRequest;
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

}
