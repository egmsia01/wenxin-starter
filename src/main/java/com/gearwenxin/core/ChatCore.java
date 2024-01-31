package com.gearwenxin.core;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.common.WenXinUtils;
import com.gearwenxin.entity.Message;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.ErrorResponse;
import com.gearwenxin.entity.response.TokenResponse;
import com.gearwenxin.exception.WenXinException;
import com.gearwenxin.subscriber.CommonSubscriber;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/21
 */
@Slf4j
public class ChatCore {

    private static final WebClient WEB_CLIENT = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    private static final String ACCESS_TOKEN_PRE = "?access_token=";

    /**
     * 非流式请求 POST
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Mono<T>
     */
    public static <T> Mono<T> monoChatPost(String url, String accessToken, Object request, Class<T> type) {
        validateParams(url, accessToken, request, type);
        log.info("monoURL => {}", url);

        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        return buildWebClient(completeUrl).post()
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(type)
                .doOnSuccess(ChatCore::handleErrResponse)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    /**
     * 流式请求 POST
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Flux<T>
     */
    public static <T> Flux<T> fluxChatPost(String url, String accessToken, Object request, Class<T> type) {
        validateParams(url, accessToken, request, type);
        log.info("fluxURL => {}", url);

        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        return buildWebClient(completeUrl).post()
                .body(BodyInserters.fromValue(request))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type)
                .doOnNext(ChatCore::handleErrResponse)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    /**
     * 非流式请求 Get
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param paramsMap   请求参数Map
     * @return Mono<T>
     */
    public static <T> Mono<T> monoChatGet(String url, String accessToken, Map<String, String> paramsMap, Class<T> type) {
        validateParams(url, accessToken, paramsMap, type);
        log.info("monoURL => {}", url);

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
        log.info("monoGet => {}", queryParams);

        return client.get()
                .retrieve()
                .bodyToMono(type)
                .doOnSuccess(ChatCore::handleErrResponse)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    /**
     * flux形式的回答，添加到历史消息中
     */
    public static <T> Flux<ChatResponse> historyFlux(String url, String token, T request, Deque<Message> messagesHistory) {
        return Flux.create(emitter -> {
            CommonSubscriber subscriber = new CommonSubscriber(emitter, messagesHistory);
            ChatCore.fluxChatPost(
                    url, token, request, ChatResponse.class
            ).subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    public static <T> Mono<ChatResponse> historyMono(String url, String token, T request, Deque<Message> messagesHistory) {
        Mono<ChatResponse> response = ChatCore.monoChatPost(
                url, token, request, ChatResponse.class
        ).subscribeOn(Schedulers.boundedElastic());

        return response.flatMap(chatResponse -> {
            assertNotNullMono(ErrorCode.SYSTEM_ERROR, "响应错误！", chatResponse.getResult(), chatResponse);

            Message messageResult = WenXinUtils.buildAssistantMessage(chatResponse.getResult());
            ChatUtils.offerMessage(messagesHistory, messageResult);

            return Mono.just(chatResponse);
        });
    }

    public static Mono<TokenResponse> getAccessTokenByAKSK(String apiKey, String secretKey) {
        assertNotBlank("api-key或secret-key为空", apiKey, secretKey);

        final String url = String.format(GET_ACCESS_TOKEN_URL, apiKey, secretKey);

        return buildWebClient(url).get()
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    @SneakyThrows
    public static String encodeURL(String component) {
        assertNotBlank(component, "EncodeURL error!");
        return URLEncoder.encode(component, "UTF-8");
    }

    private static WebClient buildWebClient(String baseUrl) {
        return WEB_CLIENT.mutate()
                .baseUrl(baseUrl)
                .build();
    }

    private static <T> void validateParams(String url, String accessToken, Object request, Class<T> type) {
        assertNotBlank(url, "model url is null");
        assertNotBlank(accessToken, "accessToken is null");
        assertNotNull(request, "request is null");
        assertNotNull(type, "response type is null");
    }

    private static Consumer<Throwable> handleWebClientError() {
        return err -> {
            log.error("请求错误 => {} {}", err instanceof WebClientResponseException
                    ? ((WebClientResponseException) err).getStatusCode() : "Unknown", err.getMessage());
            throw new WenXinException(ErrorCode.SYSTEM_NET_ERROR);
        };
    }

    private static <T> void handleErrResponse(T response) {
        assertNotNull(response, "响应异常");
        if (response instanceof ChatResponse) {
            ChatResponse chatResponse = (ChatResponse) response;
            Optional.ofNullable(chatResponse.getErrorMsg()).ifPresent(errMsg -> {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .id(chatResponse.getId())
                        .logId(chatResponse.getLogId())
                        .ebCode(chatResponse.getEbCode())
                        .errorMsg(chatResponse.getErrorMsg())
                        .errorCode(chatResponse.getErrorCode())
                        .build();
                throw new WenXinException(ErrorCode.WENXIN_ERROR, errorResponse.toString());
            });
        }
    }


}
