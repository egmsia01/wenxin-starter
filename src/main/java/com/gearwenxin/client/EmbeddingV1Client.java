package com.gearwenxin.client;

import com.gearwenxin.common.URLConstant;
import com.gearwenxin.model.BaseBot;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class EmbeddingV1Client implements BaseBot {

    private String accessToken = null;
    private static final String TAG = "PromptBotClient_";

    private static final String URL = URLConstant.PROMPT_URL;

    protected EmbeddingV1Client() {
    }

    protected abstract String getAccessToken();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getURL() {
        return URL;
    }

//    @Override
//    public String chatSingle(EmbeddingV1Request v1Request) {
//        log.info("getAccessToken => {}", getCustomAccessToken());
//        if (v1Request == null ||
//                v1Request.getContent().isEmpty()
//        ) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Mono<PromptResponse> response = ChatUtils.monoGet(
//                URL,
//                getCustomAccessToken(),
//                null,
//                PromptResponse.class);
//
//        return response.block();
//    }

}
