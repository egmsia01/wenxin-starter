package com.gearwenxin.service;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/9/24
 */
@Service
public class ErnieService {
//    public Mono<ChatResponse> chatSingle(String content) {
//
//        return Mono.justOrEmpty(content)
//                .filter(StringUtils::isNotBlank)
//                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PARAMS_ERROR)))
//                .map(WenXinUtils::buildUserMessageDeque)
//                .map(messageDeque -> ErnieRequest.builder().messages(messageDeque).build())
//                .doOnNext(request -> log.info("{}-content_singleRequest => {}", getTag(), request.toString()))
//                .flatMap(request ->
//                        ChatUtils.monoChatPost(getURL(), getCustomAccessToken(), request, ChatResponse.class)
//                );
//    }
}
