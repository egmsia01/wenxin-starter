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
    private String ChatGLM26BURL;
    private String visualGLM6BURL;

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
    public BloomZ7BClient bloomz7BClient() {
        return new BloomZ7BClient() {
            @Override
            public String getAccessToken() {
                return accessToken;
            }
        };
    }

    @Bean
    public PromptBotClient promptClient() {
        return new PromptBotClient() {
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

    @Bean
    public ChatGLM26BClient chatGLM26BClient() {
        return new ChatGLM26BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return ChatGLM26BURL;
            }
        };
    }

    @Bean
    public VisualGLM6BClient visualGLM6BClient() {
        return new VisualGLM6BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return visualGLM6BURL;
            }
        };
    }

}
