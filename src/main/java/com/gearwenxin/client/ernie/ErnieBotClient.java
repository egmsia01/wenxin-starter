package com.gearwenxin.client.ernie;

import com.gearwenxin.client.base.BaseClient;
import com.gearwenxin.common.*;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.request.ErnieRequest;
import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.model.BaseBot;
import com.gearwenxin.model.chat.ContBot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;
import static com.gearwenxin.common.Constant.MAX_SYSTEM_LENGTH;
import static com.gearwenxin.common.WenXinUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotClient extends BaseClient implements ContBot<ChatErnieRequest>, BaseBot {

    protected ErnieBotClient() {
    }

    private String accessToken = null;
    private static final String TAG = "ErnieBotClient";

    private static Map<String, Deque<Message>> ERNIE_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    private static final String URL = Constant.ERNIE_BOT_URL;

    protected abstract String getAccessToken();

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
        return Mono.justOrEmpty(Tuples.of(content, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()) && StringUtils.isNotBlank(tuple.getT2()))
                .switchIfEmpty(Mono.error(new WenXinException(ErrorCode.PARAMS_ERROR)))
                .flatMap(tuple -> {
                    Map<String, Deque<Message>> messageHistoryMap = getMessageHistoryMap();
                    Deque<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), k -> new LinkedList<>()
                    );
                    Message message = buildUserMessage(tuple.getT1());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ErnieRequest.builder()
                            .messages(messagesHistory)
                            .build();

                    log.info("{}-content_contRequest => {}", getTag(), ernieRequest.toString());

                    return ChatUtils.historyMono(getURL(), getAccessToken(), ernieRequest, messagesHistory);
                });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(String content, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(content, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT1()) && StringUtils.isNotBlank(tuple.getT2()))
                .switchIfEmpty(Mono.error(new WenXinException(ErrorCode.PARAMS_ERROR)))
                .flatMapMany(tuple -> {
                    Map<String, Deque<Message>> messageHistoryMap = getMessageHistoryMap();
                    Deque<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), k -> new LinkedList<>()
                    );
                    Message message = buildUserMessage(tuple.getT1());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ErnieRequest.builder()
                            .messages(messagesHistory)
                            .stream(true)
                            .build();

                    log.info("{}-content_contRequest_stream => {}", getTag(), ernieRequest.toString());

                    return ChatUtils.historyFlux(getURL(), getAccessToken(), ernieRequest, messagesHistory);
                });
    }

    @Override
    public Mono<ChatResponse> chatCont(ChatErnieRequest chatErnieRequest, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(chatErnieRequest, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT2()))
                .doOnNext(tuple -> validChatErnieRequest(tuple.getT1()))
                .flatMap(tuple -> {
                    Map<String, Deque<Message>> messageHistoryMap = getMessageHistoryMap();
                    Deque<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), key -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(tuple.getT1().getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ConvertUtils.toErnieRequest(tuple.getT1())
                            .messages(messagesHistory)
                            .build();

                    log.info("{}-contRequest => {}", getTag(), ernieRequest.toString());

                    return ChatUtils.historyMono(getURL(), getAccessToken(), ernieRequest, messagesHistory);
                });
    }

    @Override
    public Flux<ChatResponse> chatContOfStream(ChatErnieRequest chatErnieRequest, String msgUid) {
        return Mono.justOrEmpty(Tuples.of(chatErnieRequest, msgUid))
                .filter(tuple -> StringUtils.isNotBlank(tuple.getT2()))
                .doOnNext(tuple -> validChatErnieRequest(tuple.getT1()))
                .flatMapMany(tuple -> {
                    Map<String, Deque<Message>> messageHistoryMap = getMessageHistoryMap();
                    Deque<Message> messagesHistory = messageHistoryMap.computeIfAbsent(
                            tuple.getT2(), key -> new LinkedList<>()
                    );

                    Message message = buildUserMessage(tuple.getT1().getContent());
                    WenXinUtils.offerMessage(messagesHistory, message);

                    ErnieRequest ernieRequest = ConvertUtils.toErnieRequest(tuple.getT1())
                            .messages(messagesHistory)
                            .stream(true)
                            .build();

                    log.info("{}-contRequest_stream => {}", getTag(), ernieRequest.toString());

                    return ChatUtils.historyFlux(getURL(), getAccessToken(), ernieRequest, messagesHistory);
                });
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
        if (request.getSystem().length() > MAX_SYSTEM_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "system's length cannot be more than 1024");
        }

    }
}
