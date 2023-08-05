package com.gearwenxin.common;

import com.gearwenxin.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ge Mingjia
 * @date 2023/7/21
 */
@Slf4j
public class ChatUtils {

    private static final String ACCESS_TOKEN_PRE = "?access_token=";

    /**
     * 非流式请求 POST
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Mono<T>
     */
    public static <T> Mono<T> monoPost(
            String url,
            String accessToken,
            Object request,
            Class<T> type) {

        if (url == null || accessToken == null || request == null || type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        log.info("monoURL => {}", url);

        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        WebClient client = WebClient.builder()
                .baseUrl(completeUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.post()
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(type)
                .doOnError(WebClientResponseException.class, err -> {
                    log.error("请求错误 => {}", err.getStatusCode() + " "
                            + err.getResponseBodyAsString());
                    throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
                });
    }

    /**
     * 流式请求 POST
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Flux<T>
     */
    public static <T> Flux<T> fluxPost(
            String url,
            String accessToken,
            Object request,
            Class<T> type) {

        if (url == null || accessToken == null || request == null || type == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("fluxURL => {}", url);
        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        WebClient client = WebClient.builder()
                .baseUrl(completeUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> {
                            configurer.defaultCodecs().maxInMemorySize(-1);
                            configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder());
                            configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder());
                        })
                        .build())
                .build();

        return client.post()
                .body(BodyInserters.fromValue(request))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type)
                .doOnError(WebClientResponseException.class, err -> {
                    log.error("请求错误 => {}", err.getStatusCode() + " "
                            + err.getResponseBodyAsString());
                    throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
                });
    }

    /**
     * 非流式请求 Get
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param paramsMap   请求参数Map
     * @return Mono<T>
     */
    public static <T> Mono<T> monoGet(
            String url,
            String accessToken,
            Map<String, String> paramsMap,
            Class<T> type) {

        if (url == null || accessToken == null || paramsMap == null || paramsMap.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("monoURL => {}", url);
        WebClient.Builder clientBuilder = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        WebClient client;

        // 屎山 待优化
        paramsMap.put("access_token", accessToken);
        client = clientBuilder.build();
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
                .doOnError(WebClientResponseException.class, err -> {
                    log.error("请求错误 => {} {}", err.getStatusCode(), err.getResponseBodyAsString());
                    throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
                });
    }

    public static String encodeURL(String component) {
        if (component == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "EncodeURL error!");
        }
        return URLEncoder.encode(component, StandardCharsets.UTF_8);
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request => {}", clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(name + ": " + value)));
            return Mono.just(clientRequest);
        });
    }

}
