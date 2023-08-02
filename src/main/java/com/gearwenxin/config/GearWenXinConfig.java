package com.gearwenxin.config;

import com.gearwenxin.client.*;
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

    private String VilGCustomURL;

    @Bean
    public ErnieBotClient ernieBotClient() {
        return new ErnieBotClient() {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
    }

    @Bean
    public ErnieBotTurboClient ernieBotTurboClient() {
        return new ErnieBotTurboClient() {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
    }

    @Bean
    public Bloomz7BClient bloomz7BClient() {
        return new Bloomz7BClient() {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
    }

    @Bean
    public PromptClient promptClient() {
        return new PromptClient() {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
    }

    @Bean
    public ErnieBotVilGClient ernieBotVilGClient() {
        return new ErnieBotVilGClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return VilGCustomURL;
            }
        };
    }

}
