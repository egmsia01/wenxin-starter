package com.gearwenxin.common;

/**
 * 模型URL
 *
 * @author Ge Mingjia
 * @date 2023/7/20
 */
public interface URLConstant {

    /**
     * ErnieBot模型
     */
    String ERNIE_BOT_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=";

    /**
     * ERNIE-Bot-turbo模型
     */
    String ERNIE_BOT_TURBO_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token=";

    /**
     * BLOOMZ-7B模型
     */
    String BLOOMZ_7B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1?access_token=";

    /**
     * Embedding-V1模型
     */
    String EMBEDDING_V1_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1?access_token=";

}
