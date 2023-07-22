package com.gearwenxin;

import com.gearwenxin.client.erniebot.ErnieBot;
import com.gearwenxin.client.erniebot.ErnieBotClient;
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
    public ErnieBot ernieBot() {
        return new ErnieBotClient(accessToken);
    }
}
