package com.gearwenxin.subscriber;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

import java.util.Deque;
import java.util.StringJoiner;

import static com.gearwenxin.common.WenXinUtils.buildAssistantMessage;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public class CommonSubscriber implements Subscriber<ChatResponse>, Disposable {

    private final FluxSink<ChatResponse> emitter;
    private Subscription subscription;
    private final Deque<Message> messagesHistory;

    private final StringJoiner joiner = new StringJoiner("");

    public CommonSubscriber(FluxSink<ChatResponse> emitter, Deque<Message> messagesHistory) {
        this.emitter = emitter;
        this.messagesHistory = messagesHistory;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(50);
        log.info("onSubscribe");
    }

    @Override
    public void onNext(ChatResponse response) {
        if (isDisposed()) {
            return;
        }

        log.info("onNext");
        if (response == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ChatResponse is null !");
        }
        if (response.getResult() != null) {
            joiner.add(response.getResult());
        }
        subscription.request(50);
        emitter.next(response);
    }

    @Override
    public void onError(Throwable throwable) {
        if (isDisposed()) {
            return;
        }

        log.info("onError");
        emitter.error(throwable);
    }

    @Override
    public void onComplete() {
        if (isDisposed()) {
            return;
        }

        log.info("onComplete");
        String result = joiner.toString();
        Message message = buildAssistantMessage(result);
        WenXinUtils.offerMessage(messagesHistory, message);
        emitter.complete();
    }

    @Override
    public void dispose() {
        log.info("dispose");
        subscription.cancel();
    }

    @Override
    public boolean isDisposed() {
        return Disposable.super.isDisposed();
    }
}