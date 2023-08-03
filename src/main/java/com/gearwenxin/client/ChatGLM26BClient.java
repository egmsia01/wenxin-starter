package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.subscriber.CommonSubscriber;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class ChatGLM26BClient extends ErnieBotTurboClient {

    private String accessToken = null;
    private static final String TAG = "ChatGLM2-6BClient_";
    private static Map<String, Queue<Message>> CHAT_GLM2_6B_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 获取access-token
    @Override
    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return CHAT_GLM2_6B_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        CHAT_GLM2_6B_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return getCustomURL();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public <T> Flux<ChatResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            Flux<ChatResponse> chatResponse = ChatUtils.fluxPost(
                    getURL(),
                    getCustomAccessToken(),
                    request,
                    ChatResponse.class);
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
