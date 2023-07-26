package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.exception.BusinessException;

import com.gearwenxin.model.request.PromptRequest;
import com.gearwenxin.model.response.PromptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public class PromptBotClient implements PromptBot {

    private final String accessToken;
    private static final String TAG = "PromptBotClient_";

    public PromptBotClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public PromptResponse chatPrompt(PromptRequest promptRequest) {
        if (promptRequest == null ||
                promptRequest.getId() == null ||
                promptRequest.getId() <= 0 ||
                CollectionUtils.isEmpty(promptRequest.getParamMap())
        ) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = String.valueOf(promptRequest.getId());
        Map<String, String> paramMap = promptRequest.getParamMap();
        paramMap.put("id", id);

        Mono<PromptResponse> response = ChatUtils.monoGet(
                URLConstant.PROMPT_URL,
                accessToken,
                paramMap,
                PromptResponse.class);

        return response.block();
    }

}
