package com.gearwenxin.client.ernie;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.common.*;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.chatmodel.ChatBaseRequest;
import com.gearwenxin.entity.chatmodel.ChatErnieRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.Message;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.gearwenxin.common.WenXinUtils.assertNotBlankMono;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
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

    @Override
    public <T extends ChatBaseRequest> Flux<ChatResponse> chatsViaWebSocket(T chatRequest, String msgUid) {
        return null;
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

}
