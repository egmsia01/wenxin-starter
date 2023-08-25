package com.gearwenxin.common;

import com.gearwenxin.entity.response.TokenResponse;
import com.gearwenxin.exception.BusinessException;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Ge Mingjia
 * @date 2023/7/21
 */
@Slf4j
public class ChatUtils {

    private static final WebClient STATIC_WEB_CLIENT = WebClient.builder()
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

        WebClient client = buildWebClient(url);

        paramsMap.put("access_token", accessToken);
        String queryParams = paramsMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + encodeURL(entry.getValue()))
                .collect(Collectors.joining("&"));

        client = client.mutate()
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
                .uri("")
                .retrieve()
                .bodyToMono(type)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    public static Mono<TokenResponse> getAccessTokenByAKSK(String apiKey, String secretKey) {
        if (apiKey.isBlank() || secretKey.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;

        return buildWebClient(url).get()
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .doOnError(WebClientResponseException.class, handleWebClientError());
    }

    public static String encodeURL(String component) {
        if (component == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "EncodeURL error!");
        }
        return URLEncoder.encode(component, StandardCharsets.UTF_8);
    }

    private static WebClient buildWebClient(String baseUrl) {
        return STATIC_WEB_CLIENT.mutate()
                .baseUrl(baseUrl)
                .build();
    }

    private static <T> void validateParams(String url, String accessToken, Object request, Class<T> type) {
        if (url.isBlank() || accessToken.isBlank() || request == null || type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    private static Consumer<Throwable> handleWebClientError() {
        return err -> {
            log.error("请求错误 => {} {}", err instanceof WebClientResponseException
                    ? ((WebClientResponseException) err).getStatusCode()
                    : "Unknown", err.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
        };
    }

}
