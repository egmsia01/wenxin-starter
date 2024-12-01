package com.gearwenxin.core;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.TokenResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.schedule.TaskQueueManager;
import com.gearwenxin.schedule.entity.ModelHeader;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.gearwenxin.common.Constant.GET_ACCESS_TOKEN_URL;
import static com.gearwenxin.common.WenXinUtils.*;
import static com.gearwenxin.core.MessageHistoryManager.validateMessageRule;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/21
 */
@Slf4j
public class RequestManager {

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    private static final MessageHistoryManager messageHistoryManager = MessageHistoryManager.getInstance();
    private static final String ACCESS_TOKEN_PRE = "?access_token=";

    private static WebClient createWebClient(String baseUrl, ModelHeader header) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (header != null) {
            Optional.ofNullable(header.get_X_Ratelimit_Limit_Requests())
                    .ifPresent(value ->
                            builder.defaultHeader("X-Ratelimit-Limit-Requests", String.valueOf(value)));
            Optional.ofNullable(header.get_X_Ratelimit_Limit_Tokens())
                    .ifPresent(value ->
                            builder.defaultHeader("X-Ratelimit-Limit-Tokens", String.valueOf(value)));
            Optional.ofNullable(header.get_X_Ratelimit_Remaining_Requests())
                    .ifPresent(value ->
                            builder.defaultHeader("X-Ratelimit-Remaining-Requests", String.valueOf(value)));
            Optional.ofNullable(header.get_X_Ratelimit_Remaining_Tokens())
                    .ifPresent(value ->
                            builder.defaultHeader("X-Ratelimit-Remaining-Tokens", String.valueOf(value)));
            Optional.ofNullable(header.getAuthorization())
                    .ifPresent(value ->
                            builder.defaultHeader(HttpHeaders.AUTHORIZATION, value));
        }

        return builder.build();
    }

    public <T> Mono<T> monoPost(ModelConfig config, String accessToken, Object request, Class<T> type) {
        return monoPost(config, accessToken, request, type, null);
    }

    public <T> Mono<T> monoPost(ModelConfig config, String accessToken, Object request, Class<T> type,
                                String messageUid) {
        validateRequestParams(config.getModelUrl(), accessToken, request, type);

        String completeUrl = buildCompleteUrl(config, accessToken);
        return createWebClient(completeUrl, config.getModelHeader())
                .post()
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(type)
                .doOnSuccess(response -> handleSuccess(response, messageUid, config))
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    public <T> Flux<T> fluxPost(ModelConfig config, String accessToken, Object request, Class<T> type) {
        return fluxPost(config, accessToken, request, type, null);
    }

    public <T> Flux<T> fluxPost(ModelConfig config, String accessToken, Object request, Class<T> type,
                                String messageUid) {
        validateRequestParams(config.getModelUrl(), accessToken, request, type);

        String completeUrl = buildCompleteUrl(config, accessToken);
        return createWebClient(completeUrl, config.getModelHeader())
                .post()
                .body(BodyInserters.fromValue(request))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type)
                .doOnNext(response -> handleStreamingResponse(response, messageUid))
                .doOnError(WebClientResponseException.class, handleWebClientError())
                .doOnComplete(() -> taskManager.downModelCurrentQPS(config.getModelName()));
    }

    public <T> Mono<T> monoGet(ModelConfig config, String accessToken, Map<String, String> paramsMap,
                               Class<T> type) {
        validateRequestParams(config.getModelUrl(), accessToken, paramsMap, type);

        if (!isAuthorization(config)) {
            paramsMap.put("access_token", accessToken);
        }

        String queryParams = buildQueryParams(paramsMap);

        return createWebClient(config.getModelUrl(), config.getModelHeader())
                .get()
                .uri(uriBuilder -> uriBuilder.query(queryParams).build())
                .retrieve()
                .bodyToMono(type)
                .doOnSuccess(RequestManager::handleErrResponse)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    public <T> Flux<ChatResponse> historyFluxPost(String token, T request, Deque<Message> messagesHistory,
                                                  ModelConfig config, String msgUid) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory, config, msgUid);
            fluxPost(config, token, request, ChatResponse.class).subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    public <T> Mono<ChatResponse> historyMonoPost(String token, T request, Deque<Message> messagesHistory,
                                                  ModelConfig config, String messageUid) {
        return monoPost(config, token, request, ChatResponse.class, messageUid)
                .flatMap(chatResponse -> {
                    Message messageResult = WenXinUtils.buildAssistantMessage(chatResponse.getResult());
                    MessageHistoryManager.addMessage(messagesHistory, messageResult);
                    taskManager.downModelCurrentQPS(config.getModelName());
                    return Mono.just(chatResponse);
                });
    }

    public static Mono<TokenResponse> getAccessTokenByAKSK(String apiKey, String secretKey) {
        assertNotBlank("api-key或secret-key为空", apiKey, secretKey);

        final String url = String.format(GET_ACCESS_TOKEN_URL, apiKey, secretKey);
        return createWebClient(url, null)
                .get()
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    private String buildCompleteUrl(ModelConfig config, String accessToken) {
        return String.format("%s%s%s", config.getModelUrl(),
                isAuthorization(config) ? "" : ACCESS_TOKEN_PRE, accessToken);
    }

    private static <T> void validateRequestParams(String url, String accessToken, Object request, Class<T> type) {
        assertNotBlank(url, "model url is null");
        assertNotNull(request, "request is null");
        assertNotNull(type, "response type is null");
    }

    private static String buildQueryParams(Map<String, String> paramsMap) {
        return paramsMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + encodeURL(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private void handleSuccess(Object response, String messageUid, ModelConfig config) {
        if (!handleErrResponse(response, messageUid)) {
            taskManager.downModelCurrentQPS(config.getModelName());
        }
    }

    private static <T> void handleErrResponse(T response) {
        handleErrResponse(response, null);
    }

    private static <T> boolean handleErrResponse(T response, String messageUid) {
        assertNotNull(response, "响应异常");
        if (response instanceof ChatResponse chatResponse && chatResponse.getErrorMsg() != null) {
            log.error("响应存在错误: {}", chatResponse.getErrorMsg());
            if (messageUid != null) {
                Deque<Message> messageHistory = messageHistoryManager.getMessageHistory(messageUid);
                validateMessageRule(messageHistory);
            }
            return true;
        }
        return false;
    }

    private <T> void handleStreamingResponse(T response, String messageUid) {
        if (handleErrResponse(response, messageUid)) {
            return;
        }

        if (response instanceof ChatResponse chatResponse) {
            String text = chatResponse.getResult();
            if (text != null && text.startsWith("data:") && text.length() > 5) {
                text = text.substring(5);
            }
            if (text != null && text.endsWith("\n\n")) {
                text = text.trim();
            }
            chatResponse.setResult(text);
        }
    }

    private boolean isAuthorization(ModelConfig config) {
        return config.getModelHeader().getAuthorization() != null;
    }

    private static Consumer<Throwable> handleWebClientError() {
        return err -> {
            log.error("请求错误: {}", err.getMessage());
            throw new WenXinException(ErrorCode.SYSTEM_NET_ERROR);
        };
    }

    private static String encodeURL(String component) {
        assertNotBlank(component, "EncodeURL error!");
        return URLEncoder.encode(component, StandardCharsets.UTF_8);
    }
}