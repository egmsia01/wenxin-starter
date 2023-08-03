package com.gearwenxin.client;

import com.gearwenxin.common.ChatUtils;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.response.ChatResponse;
import com.gearwenxin.subscriber.CommonSubscriber;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
public abstract class Bloomz7BClient extends ErnieBotTurboClient {

    private String accessToken = null;
    private static final String TAG = "Bloomz7BClient_";
    private static Map<String, Queue<Message>> BLOOMZ_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();
    private static final String URL = URLConstant.BLOOMZ_7B_URL;

    @Override
    protected abstract String getAccessToken();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return BLOOMZ_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        BLOOMZ_MESSAGES_HISTORY_MAP = map;
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
