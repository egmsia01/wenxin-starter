package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.exception.BusinessException;

import com.gearwenxin.model.chatmodel.ChatPromptRequest;
import com.gearwenxin.model.request.PromptRequest;
import com.gearwenxin.model.response.PromptResponse;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class PromptClient implements Prompt, BaseBot {

    private String accessToken;
    private static final String TAG = "PromptBotClient_";

    private static final String URL = URLConstant.PROMPT_URL;

    protected PromptClient() {
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
    public PromptResponse chatPrompt(ChatPromptRequest chatPromptRequest) {
        log.info("getAccessToken => {}", getAccessToken());
        if (chatPromptRequest == null ||
                chatPromptRequest.getId() <= 0 ||
                chatPromptRequest.getParamMap().isEmpty()
        ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PromptRequest promptRequest = ConvertUtils.chatPromptReqToPromptReq(chatPromptRequest);
        String id = promptRequest.getId();
        Map<String, String> paramMap = promptRequest.getParamMap();
        paramMap.put("id", id);
        Mono<PromptResponse> response = ChatUtils.monoGet(
                URL,
                getAccessToken(),
                paramMap,
                PromptResponse.class);

        return response.block();
    }

}
