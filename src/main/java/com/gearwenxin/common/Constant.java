package com.gearwenxin.common;

/**
 * 模型URL
 *
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface Constant {

    /**
     * 最大单条内容长度
     */
    int MAX_CONTENT_LENGTH = 2000;

    /**
     * 最大所有内容总长度
     */
    int MAX_TOTAL_LENGTH = 2000;

    String ERNIE_BOT_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";
    String ERNIE_BOT_4_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro";
    String ERNIE_BOT_TURBO_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant";
    String BLOOMZ_7B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1";
    String PROMPT_URL = "https://aip.baidubce.com/rest/2.0/wenxinworkshop/api/v1/template/info";
    String QIANFAN_BLOOMZ_COMPRESSED_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/qianfan_bloomz_7b_compressed";
    String QIANFAN_CHINESE_LLAMA2_7B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/qianfan_chinese_llama_2_7b";
    String LLAMA2_7B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/llama_2_7b";
    String LLAMA2_13B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/llama_2_13b";
    String LLAMA2_70B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/llama_2_70b";
    String CHAT_GLM_6B_32K_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/chatglm2_6b_32k";
    String AQUILA_CHAT_7B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/aquilachat_7b";
    String BGE_LARGE_ZH_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/embeddings/bge_large_zh";
    String BGE_LARGE_EN_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/embeddings/bge_large_en";

}
