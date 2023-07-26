package com.gearwenxin.common;

import com.gearwenxin.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
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

        String completeUrl = url + ACCESS_TOKEN_PRE + accessToken;

        WebClient client = WebClient.builder()
                .baseUrl(completeUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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

    // 屎山 待优化
    public static <T> Mono<T> monoGet(
            String url,
            String accessToken,
            Map<String, String> paramsMap,
            Class<T> type) {

        WebClient.Builder clientBuilder = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        WebClient client;
        if (paramsMap != null && !paramsMap.isEmpty()) {
            paramsMap.put("access_token", accessToken);
            client = clientBuilder.build();
            String queryParams = paramsMap.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
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
        } else {
            client = clientBuilder.build();
        }

        return client.get()
                .uri("")
                .retrieve()
                .bodyToMono(type)
                .doOnError(WebClientResponseException.class, err -> {
                    log.error("请求错误 => {} {}", err.getStatusCode(), err.getResponseBodyAsString());
                    throw new BusinessException(ErrorCode.SYSTEM_NET_ERROR);
                });
    }


    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request => {}", clientRequest.method() + " " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(name + ": " + value)));
            return Mono.just(clientRequest);
        });
    }

}
