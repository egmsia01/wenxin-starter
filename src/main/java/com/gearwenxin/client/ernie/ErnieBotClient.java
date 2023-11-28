package com.gearwenxin.client.ernie;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.common.*;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.Constant.MAX_SYSTEM_LENGTH;
import static com.gearwenxin.common.WenXinUtils.assertNotBlankMono;

/**
 * @author Ge Mingjia

 */
@Slf4j
@Service
public class ErnieBotClient extends FullClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "ErnieBotClient";

    private static Map<String, Deque<Message>> ERNIE_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    private static final String URL = Constant.ERNIE_BOT_URL;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return ERNIE_MESSAGES_HISTORY_MAP;
    }

    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        ERNIE_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public Mono<ChatResponse> chatSingle(String content) {
        return Mono.from(this.chatSingleFunc(content, this::chatSingle));
    }

    @Override
    public Flux<ChatResponse> chatSingleOfStream(String content) {
        return Flux.from(this.chatSingleFunc(content, this::chatSingleOfStream));
    }

    @Override
    public Mono<ChatResponse> chatCont(String content, String msgUid) {
        return Mono.from(this.chatContFunc(content, msgUid, super::chatCont));
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        log.info("chatContOfStream(String content, String msgUid)");
        return Flux.from(this.chatContFunc(content, msgUid, super::chatContOfStream));
    }

    public Publisher<ChatResponse> chatContFunc(String content, String msgUid, BiFunction<ChatErnieRequest, String, Publisher<ChatResponse>> chatFunction) {
        assertNotBlankMono(content, "content is null or blank");
        assertNotBlankMono(msgUid, "msgUid is null or blank");
        log.info("=====ernie=====");
        return chatFunction.apply(buildRequest(content), msgUid);
    }

    private Publisher<ChatResponse> chatSingleFunc(String content, Function<ChatBaseRequest, Publisher<ChatResponse>> chatFunction) {
        assertNotBlankMono(content, "content is null or blank");

        return chatFunction.apply(this.buildRequest(content));
    }

    public ChatErnieRequest buildRequest(String content) {
        ChatErnieRequest chatErnieRequest = new ChatErnieRequest();
        chatErnieRequest.setContent(content);
        return chatErnieRequest;
    }

    public static void validChatErnieRequest(ChatErnieRequest request) {
        // 检查content不为空
        if (StringUtils.isBlank(request.getContent())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
        // 检查temperature和topP不都有值
        if (request.getTemperature() != null && request.getTopP() != null) {
            log.warn("Temperature and topP cannot both have value");
        }
        // 检查temperature范围
        if (request.getTemperature() != null && (request.getTemperature() <= 0 || request.getTemperature() > 1.0)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "temperature should be in (0, 1]");
        }
        // 检查topP范围
        if (request.getTopP() != null && (request.getTopP() < 0 || request.getTopP() > 1.0)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "topP should be in [0, 1]");
        }
        // 检查penaltyScore范围
        if (request.getTemperature() != null && (request.getPenaltyScore() < 1.0 || request.getPenaltyScore() > 2.0)) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "penaltyScore should be in [1, 2]");
        }
        // 检查system与function call
        if (StringUtils.isNotBlank(request.getSystem()) && request.getFunctions() != null) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "if 'function' not null, the 'system' must be null");
        }
        // 检查system长度
        if (request.getSystem() != null && request.getSystem().length() > MAX_SYSTEM_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "system's length cannot be more than 1024");
        }
    }

}
