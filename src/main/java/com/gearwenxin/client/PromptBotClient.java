package com.gearwenxin.client;

import com.gearwenxin.core.ChatCore;
import com.gearwenxin.common.ConvertUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.WenXinException;

import com.gearwenxin.entity.chatmodel.ChatPromptRequest;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.response.PromptResponse;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.PromptBot;
import com.gearwenxin.service.ChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Lazy
@Service
public class PromptBotClient implements PromptBot, BaseBot {

    ChatCore chatCore = new ChatCore();

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
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "chatPromptRequest is null or id is null or paramMap is null");
        }
        PromptRequest promptRequest = ConvertUtils.toPromptRequest(chatPromptRequest);
        Map<String, String> paramMap = promptRequest.getParamMap();
        paramMap.put("id", promptRequest.getId());

        return chatCore.monoGet(
                URL, getCustomAccessToken(), paramMap, PromptResponse.class
        );
    }

    @Override
    public <U extends ChatService, T extends ChatBaseRequest> Flux<ChatResponse> chatUsePrompt(ChatPromptRequest request, T chatRequest, U chatClient) {
        return this.chatPrompt(request).flatMapMany(response -> {
            log.debug("PromptResponse => {}", response);
            chatRequest.setContent(response.getResult().getContent());

            return chatClient.chatOnceStream(chatRequest, null);
        });
    }

}
