package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.chatmodel.ChatPromptRequest;
import com.gearwenxin.model.request.PromptRequest;
import com.gearwenxin.model.request.V1Request;
import com.gearwenxin.model.response.PromptResponse;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class V1Client implements Prompt<V1Request> {

    private String accessToken;
    private static final String TAG = "PromptBotClient_";

    private static final String URL = URLConstant.PROMPT_URL;

    protected V1Client() {
    }

    protected abstract String getAccessToken();

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public PromptResponse chatPrompt(V1Request v1Request) {
        log.info("getAccessToken => {}", getAccessToken());
        if (v1Request == null ||
                v1Request.getContent().isEmpty()
        ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Mono<PromptResponse> response = ChatUtils.monoGet(
                URL,
                getAccessToken(),
                null,
                PromptResponse.class);

        return response.block();
    }

}
