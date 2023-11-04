package com.gearwenxin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Ge Mingjia
 * @date 2023/11/1
 */
//@Data
@Component
@ConfigurationProperties("gear.wenxin")
public class WenXinProperties {

    private String accessToken;
    private String apiKey;
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

    public String getAccessToken() {
        return accessToken;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

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

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setCommon_Url(String common_url) {
        this.common_url = common_url;
    }

    public void setVilg_URL(String vilg_url) {
        this.vilg_url = vilg_url;
    }

    public void setChatGLM2_6B_URL(String chat_glm2_6B_url) {
        this.chat_glm2_6B_url = chat_glm2_6B_url;
    }

    public void setChatGLM2_6B_INT4_URL(String chat_glm2_6b_int4_url) {
        this.chat_glm2_6b_int4_url = chat_glm2_6b_int4_url;
    }

    public void setVisual_GLM_6B_URL(String visual_glm_6b_url) {
        this.visual_glm_6b_url = visual_glm_6b_url;
    }

    public void setLinly_Chinese_LLaMA2_7B_URL(String linly_chinese_llama2_7b_url) {
        this.linly_chinese_llama2_7b_url = linly_chinese_llama2_7b_url;
    }

    public void setLinly_Chinese_LLaMA2_13B_URL(String linly_chinese_llama2_13b_url) {
        this.linly_chinese_llama2_13b_url = linly_chinese_llama2_13b_url;
    }

    public void setFalcon_7B_URL(String falcon_7b_url) {
        this.falcon_7b_url = falcon_7b_url;
    }

    public void setFalcon_40B_URL(String falcon_40b_url) {
        this.falcon_40b_url = falcon_40b_url;
    }

    public void setRWKV4_World_URL(String rwky_4_world_url) {
        this.rwky_4_world_url = rwky_4_world_url;
    }

    public void setRWKV5_World_URL(String rwky_5_world_url) {
        this.rwky_5_world_url = rwky_5_world_url;
    }

    public void setRWKV4_pile_URL(String rwky_4_pile_url) {
        this.rwky_4_pile_url = rwky_4_pile_url;
    }

    public void setRWKV_Raven_14B_URL(String rwky_raven_14b_url) {
        this.rwky_raven_14b_url = rwky_raven_14b_url;
    }

    public void setOpen_LLaMA_7B_URL(String open_llama_7b_url) {
        this.open_llama_7b_url = open_llama_7b_url;
    }

    public void setMPT_7B_Instruct_URL(String mpt_7b_instruct_url) {
        this.mpt_7b_instruct_url = mpt_7b_instruct_url;
    }

    public void setMPT_30B_Instruct_URL(String mpt_30b_instruct_url) {
        this.mpt_30b_instruct_url = mpt_30b_instruct_url;
    }

    public void setDolly_12B_URL(String dolly_12b_url) {
        this.dolly_12b_url = dolly_12b_url;
    }

    public void setStable_Diffusion_V1_5_URL(String stable_diffusion_v1_5_url) {
        this.stable_diffusion_v1_5_url = stable_diffusion_v1_5_url;
    }

    public void setFlan_UL2_URL(String flan_ul2_url) {
        this.flan_ul2_url = flan_ul2_url;
    }

    public void setCerebras_GPT_13B_URL(String cerebras_gpt_13b_url) {
        this.cerebras_gpt_13b_url = cerebras_gpt_13b_url;
    }

    public void setCerebras_GPT_6_7B_URL(String cerebras_gpt_6_7b_url) {
        this.cerebras_gpt_6_7b_url = cerebras_gpt_6_7b_url;
    }

    public void setPythia_12B_URL(String pythia_12b_url) {
        this.pythia_12b_url = pythia_12b_url;
    }

    public void setPythia_6_9B_URL(String pythia_6_9b_url) {
        this.pythia_6_9b_url = pythia_6_9b_url;
    }

    public void setGPT_J_6B_URL(String gpt_j_6b_url) {
        this.gpt_j_6b_url = gpt_j_6b_url;
    }

    public void setGPT_NeoX_20B_URL(String gpt_neox_20b_url) {
        this.gpt_neox_20b_url = gpt_neox_20b_url;
    }

    public void setOA_Pythia_12B_SFT4_URL(String oa_pythia_12b_sft_4_url) {
        this.oa_pythia_12b_sft_4_url = oa_pythia_12b_sft_4_url;
    }

    public void setGPT4All_J_URL(String gpt4all_j_url) {
        this.gpt4all_j_url = gpt4all_j_url;
    }

    public void setStableLM_Alpha_7B_URL(String stablelm_alpha_7b_url) {
        this.stablelm_alpha_7b_url = stablelm_alpha_7b_url;
    }

    public void setStarCoder_URL(String star_coder_url) {
        this.star_coder_url = star_coder_url;
    }

    public void setCustom_Model_URL(String custom_model_url) {
        this.custom_model_url = custom_model_url;
    }

    public void setSQLCoder_7B(String sql_coder_7b_url) {
        this.sql_coder_7b_url = sql_coder_7b_url;
    }

}
