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
    String ERNIE_BOT_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions";

    /**
     * ERNIE-Bot-turbo模型
     */
    String ERNIE_BOT_TURBO_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant";

    /**
     * BLOOMZ-7B模型
     */
    String BLOOMZ_7B_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/bloomz_7b1";

    /**
     * Peompt模板模型
     */
    String PROMPT_URL = "https://aip.baidubce.com/rest/2.0/wenxinworkshop/api/v1/template/info";

}
