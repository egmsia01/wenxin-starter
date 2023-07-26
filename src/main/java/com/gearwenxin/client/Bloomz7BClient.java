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
public class Bloomz7BClient extends ErnieBotTurboClient {

    private final String accessToken;
    private static final String TAG = "Bloomz7BClient_";
    private static final Map<String, Queue<Message>> MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    public Bloomz7BClient(String accessToken) {
        super(accessToken);
        this.accessToken = accessToken;
    }

    @Override
    public <T> Flux<ChatResponse> historyFlux(T request, Queue<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            Flux<ChatResponse> chatResponse = ChatUtils.fluxPost(
                    URLConstant.BLOOMZ_7B_URL,
                    accessToken,
                    request,
                    ChatResponse.class);
            chatResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }
}
