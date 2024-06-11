package com.gearwenxin.subscriber;

import com.gearwenxin.common.Constant;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.core.MessageHistoryManager;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

import java.util.Deque;
import java.util.Optional;
import java.util.StringJoiner;

import static com.gearwenxin.common.WenXinUtils.assertNotNull;
import static com.gearwenxin.common.WenXinUtils.buildAssistantMessage;
import static com.gearwenxin.core.MessageHistoryManager.validateMessageRule;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
public class CommonSubscriber implements Subscriber<ChatResponse>, Disposable {

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    private final FluxSink<ChatResponse> emitter;
    private Subscription subscription;
    private final Deque<Message> messagesHistory;
    @Resource
    private MessageService messageService;
    private final ModelConfig modelConfig;
    private final String msgUid;

    private final StringJoiner joiner = new StringJoiner("");

    public CommonSubscriber(FluxSink<ChatResponse> emitter, Deque<Message> messagesHistory,
                            ModelConfig modelConfig, String msgUid) {
        this.emitter = emitter;
        this.messagesHistory = messagesHistory;
        this.modelConfig = modelConfig;
        this.msgUid = msgUid;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
        log.debug("onSubscribe");
    }

    @Override
    public void onNext(ChatResponse response) {
        if (isDisposed()) {
            return;
        }
        // 中断对话
        if (Constant.INTERRUPT_MAP.get(msgUid)) {
            log.debug("interrupted");
            dispose();
            return;
        }

        assertNotNull(response, "chat response is null");

        log.debug("onNext...");

        Optional.ofNullable(response.getResult()).ifPresent(joiner::add);
        subscription.request(1);
        emitter.next(response);
    }

    @Override
    public void onError(Throwable throwable) {
        taskManager.downModelCurrentQPS(modelConfig.getModelName());
        validateMessageRule(messagesHistory);
        if (isDisposed()) {
            return;
        }
        log.debug("onError");
        emitter.error(throwable);
    }

    @Override
    public void onComplete() {
        taskManager.downModelCurrentQPS(modelConfig.getModelName());
        if (isDisposed()) {
            return;
        }
        log.debug("onComplete");
        String result = joiner.toString();
        Optional.ofNullable(result).filter(StringUtils::isNotBlank).ifPresent(r -> {
            Message message = buildAssistantMessage(r);
            MessageHistoryManager.addMessage(messagesHistory, message);
            log.debug("add message onComplete");
        });
        emitter.complete();
    }

    @Override
    public void dispose() {
        taskManager.downModelCurrentQPS(modelConfig.getModelName());
        log.debug("dispose");
        subscription.cancel();
    }

    @Override
    public boolean isDisposed() {
        return Disposable.super.isDisposed();
    }

}