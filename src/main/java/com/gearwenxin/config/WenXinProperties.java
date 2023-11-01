package com.gearwenxin.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ge Mingjia
 * @date 2023/11/1
 */
@Setter
@Component
@ConfigurationProperties("gear.wenxin")
public class WenXinProperties {

    @Getter
    private String accessToken;
    @Getter
    private String apiKey;
    @Getter
    private String secretKey;

    private String common_url;
    private String vilg_url;
    private String chat_glm2_6B_url;
    private String chat_glm2_6b_int4_url;
    private String visual_glm_6b_url;
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
    private String sql_coder_7b_url;

    public String getCommon_Url() {
        return common_url;
    }

    public String getVilg_URL() {
        return vilg_url;
    }

    public String getChatGLM2_6B_URL() {
        return chat_glm2_6B_url;
    }

    public String getChatGLM2_6B_INT4_URL() {
        return chat_glm2_6b_int4_url;
    }

    public String getVisual_GLM_6B_URL() {
        return visual_glm_6b_url;
    }

    public String getLinly_Chinese_LLaMA2_7B_URL() {
        return linly_chinese_llama2_7b_url;
    }

    public String getLinly_Chinese_LLaMA2_13B_URL() {
        return linly_chinese_llama2_13b_url;
    }

    public String getFalcon_7B_URL() {
        return falcon_7b_url;
    }

    public String getFalcon_40B_URL() {
        return falcon_40b_url;
    }

    public String getRWKV4_World_URL() {
        return rwky_4_world_url;
    }

    public String getRWKV5_World_URL() {
        return rwky_5_world_url;
    }

    public String getRWKV4_pile_URL() {
        return rwky_4_pile_url;
    }

    public String getRWKV_Raven_14B_URL() {
        return rwky_raven_14b_url;
    }

    public String getOpen_LLaMA_7B_URL() {
        return open_llama_7b_url;
    }

    public String getMPT_7B_Instruct_URL() {
        return mpt_7b_instruct_url;
    }

    public String getMPT_30B_Instruct_URL() {
        return mpt_30b_instruct_url;
    }

    public String getDolly_12B_URL() {
        return dolly_12b_url;
    }

    public String getStable_Diffusion_V1_5_URL() {
        return stable_diffusion_v1_5_url;
    }

    public String getFlan_UL2_URL() {
        return flan_ul2_url;
    }

    public String getCerebras_GPT_13B_URL() {
        return cerebras_gpt_13b_url;
    }

    public String getCerebras_GPT_6_7B_URL() {
        return cerebras_gpt_6_7b_url;
    }

    public String getPythia_12B_URL() {
        return pythia_12b_url;
    }

    public String getPythia_6_9B_URL() {
        return pythia_6_9b_url;
    }

    public String getGPT_J_6B_URL() {
        return gpt_j_6b_url;
    }

    public String getGPT_NeoX_20B_URL() {
        return gpt_neox_20b_url;
    }

    public String getOA_Pythia_12B_SFT4_URL() {
        return oa_pythia_12b_sft_4_url;
    }

    public String getGPT4All_J_URL() {
        return gpt4all_j_url;
    }

    public String getStableLM_Alpha_7B_URL() {
        return stablelm_alpha_7b_url;
    }

    public String getStarCoder_URL() {
        return star_coder_url;
    }

    public String getCustom_Model_URL() {
        return custom_model_url;
    }

    public String getSQLCoder_7B() {
        return sql_coder_7b_url;
    }

}
