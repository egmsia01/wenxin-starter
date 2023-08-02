package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.exception.BusinessException;

import com.gearwenxin.model.Message;
import com.gearwenxin.model.chatmodel.ChatPromptRequest;
import com.gearwenxin.model.request.PromptRequest;
import com.gearwenxin.model.response.PromptResponse;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Queue;

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
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        log.warn(TAG + "PromptClient not have MessageHistoryMap");
        return null;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        log.warn(TAG + "PromptClient not need init");
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public PromptResponse chatPrompt(ChatPromptRequest chatPromptRequest) {
        log.info(TAG + "getAccessToken => {}", getAccessToken());
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
