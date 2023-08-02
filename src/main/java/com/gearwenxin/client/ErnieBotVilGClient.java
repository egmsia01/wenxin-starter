package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.chatmodel.ChatTurbo7BRequest;
import com.gearwenxin.model.request.Turbo7BRequest;
import com.gearwenxin.model.response.ChatResponse;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static com.gearwenxin.common.WenXinUtils.*;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotVilGClient implements CommonBot<ChatTurbo7BRequest>, BaseBot, ImageBot {

    private String accessToken;
    private static final String TAG = "ErnieBotVilGClient_";
    public static final String PREFIX_MSG_HISTORY_MONO = "Mono_";
    public static final String PREFIX_MSG_HISTORY_FLUX = "Flux_";
    private static final String URL = URLConstant.ERNIE_BOT_TURBO_URL;

    // 每个模型的历史消息Map
    private static Map<String, Queue<Message>> TURBO_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    protected ErnieBotVilGClient() {
    }

    protected abstract String getAccessToken();

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return TURBO_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        TURBO_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public String chatImage(String content, int width, int height) {
        if (StringUtils.isEmpty(content)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Queue<Message> messageQueue = buildUserMessageQueue(content);
        Turbo7BRequest request = new Turbo7BRequest();
        request.setMessages(messageQueue);
        log.info(TAG + "content_singleRequest => {}", request.toString());

        Mono<String> response = ChatUtils.monoPost(
                getURL(),
                getAccessToken(),
                request,
                String.class);
        return response.block();
    }

//    public void validBaseRequest(ChatTurbo7BRequest request) {
//        // 检查content不为空
//        if (StringUtils.isEmpty(request.getContent())) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
//        }
//        // 检查单个content长度
//        if (request.getContent().length() > MAX_CONTENT_LENGTH) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
//        }
//    }
//
//    public <T> Flux<ChatResponse> historyFlux(T request, Queue<Message> messagesHistory) {
//        return Flux.create(emitter -> {
//            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
//            Flux<ChatResponse> chatResponse = ChatUtils.fluxPost(
//                    getURL(),
//                    getAccessToken(),
//                    request,
//                    ChatResponse.class);
//            chatResponse.subscribe(subscriber);
//            emitter.onDispose(subscriber);
//        });
//    }

}
