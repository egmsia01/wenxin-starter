package com.gearwenxin.config;

import com.gearwenxin.client.*;
import com.gearwenxin.client.linly.LinlyChineseLLaMA213BClient;
import com.gearwenxin.client.linly.LinlyChineseLLaMA27BClient;
import com.gearwenxin.client.llama2.Llama213BClient;
import com.gearwenxin.client.llama2.Llama270BClient;
import com.gearwenxin.client.llama2.Llama27BClient;
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
    private String commonUrl;
    private String vilgUrl;
    private String chatGlm26bUrl;
    private String visualGlm6bUrl;
    private String llama27bUrl;
    private String llama213bUrl;
    private String llama270bUrl;
    private String linlyChineseLlama27b;
    private String linlyChineseLlama213b;

    @Bean
    public CommonModelClient commonModelClient() {
        return new CommonModelClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return commonUrl;
            }
        };
    }

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
                return vilgUrl;
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
                return chatGlm26bUrl;
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
                return visualGlm6bUrl;
            }
        };
    }

    @Bean
    public Llama27BClient llama27BClient() {
        return new Llama27BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return llama27bUrl;
            }
        };
    }

    @Bean
    public Llama213BClient llama213BClient() {
        return new Llama213BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return llama213bUrl;
            }
        };
    }

    @Bean
    public Llama270BClient llama270BClient() {
        return new Llama270BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return llama270bUrl;
            }
        };
    }

    @Bean
    public LinlyChineseLLaMA27BClient linlyChineseLLaMA27BClient() {
        return new LinlyChineseLLaMA27BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return linlyChineseLlama27b;
            }
        };
    }

    @Bean
    public LinlyChineseLLaMA213BClient linlyChineseLLaMA213BClient() {
        return new LinlyChineseLLaMA213BClient() {
            @Override
            protected String getAccessToken() {
                return accessToken;
            }

            @Override
            protected String getCustomURL() {
                return linlyChineseLlama213b;
            }
        };
    }

}
