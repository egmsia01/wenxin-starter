package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.exception.WenXinException;

import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.PromptBot;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
@Lazy
@Service
public class PromptBotClient implements PromptBot, BaseBot {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "Prompt-Bot-Client";

    private static final String URL = Constant.PROMPT_URL;

    public String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

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
            throw new WenXinException(ErrorCode.PARAMS_ERROR);
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
