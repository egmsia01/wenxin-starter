package com.gearwenxin.config;

import com.gearwenxin.client.*;
import com.gearwenxin.client.cerebras.CerebrasGPT13BClient;
import com.gearwenxin.client.cerebras.CerebrasGPT6_7BClient;
import com.gearwenxin.client.ernie.ErnieBot4Client;
import com.gearwenxin.client.ernie.ErnieBotClient;
import com.gearwenxin.client.ernie.ErnieBotTurboClient;
import com.gearwenxin.client.ernie.ErnieBotVilGClient;
import com.gearwenxin.client.falcon.Falcon40BClient;
import com.gearwenxin.client.falcon.Falcon7BClient;
import com.gearwenxin.client.glm.ChatGLM26BClient;
import com.gearwenxin.client.glm.VisualGLM6BClient;
import com.gearwenxin.client.gpt.GPT4AllJClient;
import com.gearwenxin.client.gpt.GPTJ6BClient;
import com.gearwenxin.client.gpt.GPTNeoX20BClient;
import com.gearwenxin.client.linly.LinlyChineseLLaMA213BClient;
import com.gearwenxin.client.linly.LinlyChineseLLaMA27BClient;
import com.gearwenxin.client.llama2.Llama213BClient;
import com.gearwenxin.client.llama2.Llama270BClient;
import com.gearwenxin.client.llama2.Llama27BClient;
import com.gearwenxin.client.llama2.OpenLLaMA7BClient;
import com.gearwenxin.client.mpt.MPT30BInstructClient;
import com.gearwenxin.client.mpt.MPT7BInstructClient;
import com.gearwenxin.client.pythia.OAPythia12BSFT4Client;
import com.gearwenxin.client.pythia.Pythia12BClient;
import com.gearwenxin.client.pythia.Pythia6_9BClient;
import com.gearwenxin.client.rwkv.RWKV4Pile14BClient;
import com.gearwenxin.client.rwkv.RWKV4WorldClient;
import com.gearwenxin.client.rwkv.RWKV5WorldClient;
import com.gearwenxin.client.rwkv.RWKVRaven14BClient;
import com.gearwenxin.client.stable.StableDiffusionV1_5Client;
import com.gearwenxin.client.stable.StableLMAlpha7BClient;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.entity.response.TokenResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ge Mingjia
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties("gear.wenxin")
public class GearWenXinConfig implements CommandLineRunner {

    private String access_token;
    private String api_key;
    private String secret_key;
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
    private String falcon_40b_url;
    private String rwky_4_world_url;
    private String rwky_5_world_url;
    private String rwky_4_pile_url;
    private String rwky_raven_14b_url;
    private String open_llama_7b_url;
    private String mpt_7b_instruct_url;
    private String mpt_30b_instruct_url;
    private String dolly_12b_url;
    private String stable_diffusion_v1_5_url;
    private String flan_ul2_url;
    private String cerebras_gpt_13b_url;
    private String cerebras_gpt_6_7b_url;
    private String pythia_12b_url;
    private String pythia_6_9b_url;
    private String gpt_j_6b_url;
    private String gpt_neox_20b_url;
    private String oa_pythia_12b_sft_4_url;
    private String gpt4all_j_url;
    private String stablelm_alpha_7b_url;
    private String star_coder_url;
    private String custom_model_url;

    @Override
    public void run(String... args) {
        if (api_key == null || secret_key == null) {
            return;
        }
        ChatUtils.getAccessTokenByAKSK(api_key, secret_key)
                .doOnNext(tokenResponse -> {
                    if (tokenResponse.getAccessToken() == null && access_token == null) {
                        log.warn("api_key or secret_key error！");
                    }
                })
                .map(TokenResponse::getAccessToken)
                .doOnNext(this::setAccessToken)
                .subscribe();
    }

    private void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

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
    public ErnieBot4Client ernieBot4Client() {
        return new ErnieBot4Client() {
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
    public Falcon40BClient falcon40BClient() {
        return new Falcon40BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return falcon_40b_url;
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
    public RWKV5WorldClient rwkv5WorldClient() {
        return new RWKV5WorldClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return rwky_5_world_url;
            }
        };
    }

    @Bean
    public RWKV4Pile14BClient rwkv4Pile14BClient() {
        return new RWKV4Pile14BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return rwky_4_pile_url;
            }
        };
    }

    @Bean
    public RWKVRaven14BClient rwkvRaven14BClient() {
        return new RWKVRaven14BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return rwky_raven_14b_url;
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

    @Bean
    public MPT7BInstructClient mpt7BInstructClient() {
        return new MPT7BInstructClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return mpt_7b_instruct_url;
            }
        };
    }

    @Bean
    public MPT30BInstructClient mpt30BInstructClient() {
        return new MPT30BInstructClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return mpt_30b_instruct_url;
            }
        };
    }

    @Bean
    public Dolly12BClient dolly12BClient() {
        return new Dolly12BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return dolly_12b_url;
            }
        };
    }

    @Bean
    public StableDiffusionV1_5Client stableDiffusionV15Client() {
        return new StableDiffusionV1_5Client() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return stable_diffusion_v1_5_url;
            }
        };
    }

    @Bean
    public FlanUL2Client flanUL2Client() {
        return new FlanUL2Client() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return flan_ul2_url;
            }
        };
    }

    @Bean
    public CerebrasGPT13BClient cerebrasGPT13BClient() {
        return new CerebrasGPT13BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return cerebras_gpt_13b_url;
            }
        };
    }

    @Bean
    public CerebrasGPT6_7BClient cerebrasGPT67BClient() {
        return new CerebrasGPT6_7BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return cerebras_gpt_6_7b_url;
            }
        };
    }

    @Bean
    public Pythia12BClient pythia12BClient() {
        return new Pythia12BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return pythia_12b_url;
            }
        };
    }

    @Bean
    public Pythia6_9BClient pythia69BClient() {
        return new Pythia6_9BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return pythia_6_9b_url;
            }
        };
    }

    @Bean
    public GPTJ6BClient gptJ6BClient() {
        return new GPTJ6BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return gpt_j_6b_url;
            }
        };
    }

    @Bean
    public GPTNeoX20BClient gptNeoX20BClient() {
        return new GPTNeoX20BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return gpt_neox_20b_url;
            }
        };
    }

    @Bean
    public OAPythia12BSFT4Client oaPythia12BSFT4Client() {
        return new OAPythia12BSFT4Client() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return oa_pythia_12b_sft_4_url;
            }
        };
    }

    @Bean
    public GPT4AllJClient gpt4AllJClient() {
        return new GPT4AllJClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return gpt4all_j_url;
            }
        };
    }

    @Bean
    public StableLMAlpha7BClient stableLMAlpha7BClient() {
        return new StableLMAlpha7BClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return stablelm_alpha_7b_url;
            }
        };
    }

    @Bean
    public StarCoderClient starCoderClient() {
        return new StarCoderClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return star_coder_url;
            }
        };
    }

    @Bean
    public CustomModelClient customModelClient() {
        return new CustomModelClient() {
            @Override
            protected String getAccessToken() {
                return access_token;
            }

            @Override
            protected String getCustomURL() {
                return custom_model_url;
            }
        };
    }

}
