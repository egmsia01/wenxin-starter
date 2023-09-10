package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.Constant;
import com.gearwenxin.exception.BusinessException;

import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.PromptBot;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Queue;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class PromptBotClient implements PromptBot, BaseBot {

    protected PromptBotClient() {
    }

    private String accessToken = null;
    private static final String TAG = "Prompt-Bot-Client_";

    private static final String URL = Constant.PROMPT_URL;

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

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Mono<PromptResponse> chatPrompt(ChatPromptRequest chatPromptRequest) {
        if (chatPromptRequest == null ||
                chatPromptRequest.getId() <= 0 ||
                CollectionUtils.isEmpty(chatPromptRequest.getParamMap())
        ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PromptRequest promptRequest = ConvertUtils.toPromptRequest(chatPromptRequest);
        String id = promptRequest.getId();
        Map<String, String> paramMap = promptRequest.getParamMap();
        paramMap.put("id", id);

        return ChatUtils.monoChatGet(
                URL, getCustomAccessToken(), paramMap, PromptResponse.class
        );
    }

}
