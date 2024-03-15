package com.gearwenxin.core;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.config.ModelConfig;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ErrorResponse;
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

import java.net.URI;
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
public class WebManager {

    private final TaskQueueManager taskManager = TaskQueueManager.getInstance();

    MessageHistoryManager messageHistoryManager = MessageHistoryManager.getInstance();

    private static final String ACCESS_TOKEN_PRE = "?access_token=";

    private static WebClient buildWebClient(String baseUrl) {
        return buildWebClient(baseUrl, null);
    }

    private static WebClient buildWebClient(String baseUrl, ModelHeader header) {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (header == null) {
            return builder.build();
        }

        if (header.get_X_Ratelimit_Limit_Requests() != null) {
            builder.defaultHeader("X-Ratelimit-Limit-Requests", String.valueOf(header.get_X_Ratelimit_Limit_Requests()));
        }
        if (header.get_X_Ratelimit_Limit_Tokens() != null) {
            builder.defaultHeader("X-Ratelimit-Limit-Tokens", String.valueOf(header.get_X_Ratelimit_Limit_Tokens()));
        }
        if (header.get_X_Ratelimit_Remaining_Requests() != null) {
            builder.defaultHeader("X-Ratelimit-Remaining-Requests", String.valueOf(header.get_X_Ratelimit_Remaining_Requests()));
        }
        if (header.get_X_Ratelimit_Remaining_Tokens() != null) {
            builder.defaultHeader("X-Ratelimit-Remaining-Tokens", String.valueOf(header.get_X_Ratelimit_Remaining_Tokens()));
        }

        return builder.build();
    }

    public <T> Mono<T> monoPost(ModelConfig config, String accessToken, Object request, Class<T> type) {
        return monoPost(config, accessToken, request, type, null);
    }

    /**
     * 非流式请求 POST
     *
     * @param config      Model配置
     * @param accessToken accessToken
     * @param request     请求类
     * @return Mono<T>
     */
    public <T> Mono<T> monoPost(ModelConfig config, String accessToken, Object request, Class<T> type, String messageUid) {
        String url = config.getModelUrl();
        validateParams(url, accessToken, request, type);
        log.debug("model url: {}", url);

        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        return buildWebClient(completeUrl, config.getModelHeader())
                .post()
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(type)
                .doOnSuccess(response -> {
                    handleErrResponse(response);
                    taskManager.downModelCurrentQPS(config.getModelName());
                })
                .doOnError(WebClientResponseException.class, e -> {
                    if (messageUid == null) {
                        handleWebClientError().accept(e);
                        return;
                    }
                    Deque<Message> messageHistory = messageHistoryManager.getMessageHistory(messageUid);
                    validateMessageRule(messageHistory);
                    handleWebClientError().accept(e);
                });
    }

    /**
     * 流式请求 POST
     *
     * @param config      Model配置
     * @param accessToken accessToken
     * @param request     请求类
     * @return Flux<T>
     */
    public <T> Flux<T> fluxPost(ModelConfig config, String accessToken, Object request, Class<T> type) {
        String url = config.getModelUrl();
        validateParams(url, accessToken, request, type);
        log.debug("model url: {}", url);

        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        return buildWebClient(completeUrl, config.getModelHeader())
                .post()
                .body(BodyInserters.fromValue(request))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type)
                .doOnNext(response -> {
                    handleErrResponse(response);
                    ChatResponse chatResponse = (ChatResponse) response;
                    String text = ((ChatResponse) response).getResult();
                    if (text.startsWith("data:") && text.length() > 5) {
                        text = text.substring(5);
                    }
                    if (text.endsWith("\n\n")) {
                        text = text.substring(0, text.length() - 2);
                        text = text.trim();
                    }
                    chatResponse.setResult(text);
                })
                .doOnError(WebClientResponseException.class, handleWebClientError())
                .doOnComplete(() -> taskManager.downModelCurrentQPS(config.getModelName()));
    }

    /**
     * 非流式请求 Get
     *
     * @param config      Model配置
     * @param accessToken accessToken
     * @param paramsMap   请求参数Map
     * @return Mono<T>
     */
    public <T> Mono<T> monoGet(ModelConfig config, String accessToken, Map<String, String> paramsMap, Class<T> type) {
        String url = config.getModelUrl();
        validateParams(url, accessToken, paramsMap, type);
        log.debug("model url: {}", url);

        paramsMap.put("access_token", accessToken);
        String queryParams = paramsMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + encodeURL(entry.getValue()))
                .collect(Collectors.joining("&"));

        WebClient client = buildWebClient(url).mutate()
                .filter((request, next) -> {
                    String uriWithQueryParams = request.url() + "?" + queryParams;
                    ClientRequest filteredRequest = ClientRequest.from(request)
                            .url(URI.create(uriWithQueryParams))
                            .build();
                    return next.exchange(filteredRequest);
                })
                .build();

        return client.get()
                .retrieve()
                .bodyToMono(type)
                .doOnSuccess(WebManager::handleErrResponse)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    public <T> Flux<ChatResponse> historyFluxPost(String token, T request, Deque<Message> messagesHistory, ModelConfig config) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory, config);
            fluxPost(config, token, request, ChatResponse.class).subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    public <T> Mono<ChatResponse> historyMonoPost(String token, T request, Deque<Message> messagesHistory, ModelConfig config, String messageUid) {
        Mono<ChatResponse> response = monoPost(config, token, request, ChatResponse.class, messageUid);

        return response.flatMap(chatResponse -> {
            Message messageResult = WenXinUtils.buildAssistantMessage(chatResponse.getResult());
            MessageHistoryManager.addMessage(messagesHistory, messageResult);
            taskManager.downModelCurrentQPS(config.getModelName());
            return Mono.just(chatResponse);
        });
    }

    public static Mono<TokenResponse> getAccessTokenByAKSK(String apiKey, String secretKey) {
        assertNotBlank("api-key或secret-key为空", apiKey, secretKey);

        final String url = String.format(GET_ACCESS_TOKEN_URL, apiKey, secretKey);

        return buildWebClient(url)
                .get()
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }

    public static String encodeURL(String component) {
        assertNotBlank(component, "EncodeURL error!");
        return URLEncoder.encode(component, StandardCharsets.UTF_8);
    }

    private static <T> void validateParams(String url, String accessToken, Object request, Class<T> type) {
        assertNotBlank(url, "model url is null");
        assertNotBlank(accessToken, "accessToken is null");
        assertNotNull(request, "request is null");
        assertNotNull(type, "response type is null");
    }

    private static Consumer<Throwable> handleWebClientError() {
        return err -> {
            log.error("请求错误: {} {}", err instanceof WebClientResponseException
                    ? ((WebClientResponseException) err).getStatusCode() : "Unknown", err.getMessage());
            throw new WenXinException(ErrorCode.SYSTEM_NET_ERROR);
        };
    }

    private static <T> void handleErrResponse(T response) {
        assertNotNull(response, "响应异常");
        if (response instanceof ChatResponse chatResponse) {
            Optional.ofNullable(chatResponse.getErrorMsg()).ifPresent(errMsg -> {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .id(chatResponse.getId())
                        .logId(chatResponse.getLogId())
                        .ebCode(chatResponse.getEbCode())
                        .errorMsg(chatResponse.getErrorMsg())
                        .errorCode(chatResponse.getErrorCode())
                        .build();
                log.error("响应存在错误: {}", errorResponse);
            });
        }
    }

}
