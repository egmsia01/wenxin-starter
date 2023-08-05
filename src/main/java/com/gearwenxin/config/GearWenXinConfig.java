package com.gearwenxin.config;

import com.gearwenxin.client.*;
import com.gearwenxin.client.linly.LinlyChineseLLaMA213BClient;
import com.gearwenxin.client.linly.LinlyChineseLLaMA27BClient;
import com.gearwenxin.client.llama2.Llama213BClient;
import com.gearwenxin.client.llama2.Llama270BClient;
import com.gearwenxin.client.llama2.Llama27BClient;
import com.gearwenxin.client.rwkv.RWKV4WorldClient;
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

    private String access_token;
    private String common_url;
    private String vilg_url;
    private String chat_glm2_6b_url;
    private String visual_glm_6b_url;
    private String llama2_7b_url;
    private String llama2_13b_url;
    private String llama2_70b_url;
    private String linly_chinese_llama2_7b_url;
    private String linly_chinese_llama2_13b_url;
    private String falcon_7b_url;
    private String rwky_4_world_url;
    private String open_llama_7b_url;

    @Bean
    public CommonModelClient commonModelClient() {
        return new CommonModelClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return common_url;
            }
        };
    }

    @Bean
    public ErnieBotClient ernieBotClient() {
        return new ErnieBotClient() {
            @Override
            public String getAccessToken() {
                return access_token;
            }
        };
    }

    @Bean
    public ErnieBotTurboClient ernieBotTurboClient() {
        return new ErnieBotTurboClient() {
            @Override
            public String getAccessToken() {
                return access_token;
            }
        };
    }

    @Bean
    public BloomZ7BClient bloomz7BClient() {
        return new BloomZ7BClient() {
            @Override
            public String getAccessToken() {
                return access_token;
            }
        };
    }

    @Bean
    public PromptBotClient promptClient() {
        return new PromptBotClient() {
            @Override
            public String getAccessToken() {
                return access_token;
            }
        };
    }

    @Bean
    public ErnieBotVilGClient ernieBotVilGClient() {
        return new ErnieBotVilGClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return vilg_url;
            }
        };
    }

    @Bean
    public ChatGLM26BClient chatGLM26BClient() {
        return new ChatGLM26BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return chat_glm2_6b_url;
            }
        };
    }

    @Bean
    public VisualGLM6BClient visualGLM6BClient() {
        return new VisualGLM6BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return visual_glm_6b_url;
            }
        };
    }

    @Bean
    public Llama27BClient llama27BClient() {
        return new Llama27BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return llama2_7b_url;
            }
        };
    }

    @Bean
    public Llama213BClient llama213BClient() {
        return new Llama213BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return llama2_13b_url;
            }
        };
    }

    @Bean
    public Llama270BClient llama270BClient() {
        return new Llama270BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return llama2_70b_url;
            }
        };
    }

    @Bean
    public LinlyChineseLLaMA27BClient linlyChineseLLaMA27BClient() {
        return new LinlyChineseLLaMA27BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return linly_chinese_llama2_7b_url;
            }
        };
    }

    @Bean
    public LinlyChineseLLaMA213BClient linlyChineseLLaMA213BClient() {
        return new LinlyChineseLLaMA213BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return linly_chinese_llama2_13b_url;
            }
        };
    }

    @Bean
    public Falcon7BClient falcon7BClient() {
        return new Falcon7BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return falcon_7b_url;
            }
        };
    }

    @Bean
    public RWKV4WorldClient rwkv4WorldClient() {
        return new RWKV4WorldClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return rwky_4_world_url;
            }
        };
    }

    @Bean
    public OpenLLaMA7BClient openLLaMA7BClient() {
        return new OpenLLaMA7BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return open_llama_7b_url;
            }
        };
    }
}
