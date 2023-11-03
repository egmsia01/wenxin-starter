package com.gearwenxin.client.ernie;

import com.gearwenxin.client.base.BaseClient;
import com.gearwenxin.common.*;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.BaseRequest;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.ContBot;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.Constant.MAX_SYSTEM_LENGTH;
import static com.gearwenxin.common.WenXinUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
@Service
public class ErnieBotClient extends BaseClient implements ContBot<ChatErnieRequest>, BaseBot {

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
    public Mono<ChatResponse> chatCont(String content, String msgUid) {
        return Mono.defer(() -> Mono.from(chatContFunc(content, msgUid, this::chatCont)));
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Flux.defer(() -> chatContFunc(content, msgUid, this::chatContOfStream));
    }

    @Override
    public Mono<ChatResponse> chatCont(ChatErnieRequest chatErnieRequest, String msgUid) {
        return Mono.from(chatContProcess(chatErnieRequest, msgUid, false));
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatErnieRequest chatBaseRequest, String msgUid) {
        return Flux.from(chatContProcess(chatBaseRequest, msgUid, true));
    }

    private Publisher<ChatResponse> chatContProcess(ChatErnieRequest chatErnieRequest, String msgUid, boolean stream) {
        return Mono.justOrEmpty(chatErnieRequest)
                .filter(request -> StringUtils.isNotBlank(msgUid))
                .switchIfEmpty(Mono.error(() -> new WenXinException(ErrorCode.PARAMS_ERROR)))
                .doOnNext(ErnieBotClient::validChatErnieRequest)
                .flatMapMany(request -> {
                    Deque<Message> messagesHistory = getMessageHistoryMap().computeIfAbsent(
                            msgUid, key -> new LinkedList<>()
                    );

                    Message message = WenXinUtils.buildUserMessage(request.getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ConvertUtils.toErnieRequest(request)
                            .messages(messagesHistory)
                            .stream(stream)
                            .build();

                    String logMessage = stream ? "{}-contRequest-stream => {}" : "{}-contRequest => {}";
                    log.info(logMessage, getTag(), ernieRequest);

                    if (stream) {
                        return ChatUtils.historyFlux(getURL(), getCustomAccessToken(), ernieRequest, messagesHistory);
                    } else {
                        return ChatUtils.historyMono(getURL(), getCustomAccessToken(), ernieRequest, messagesHistory);
                    }
                });
    }

    private Publisher<ChatResponse> chatContFunc(String content, String msgUid, BiFunction<ChatErnieRequest, String, Publisher<ChatResponse>> chatFunction) {
        assertNotBlankMono(content, "content is null or blank");
        assertNotBlankMono(msgUid, "msgUid is null or blank");

        return chatFunction.apply(buildErnieRequest(content), msgUid);
    }

    private ChatErnieRequest buildErnieRequest(String content) {
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
