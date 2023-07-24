package com.gearwenxin.common;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Ge Mingjia
 * @date 2023/7/21
 */
public class ChatUtils {

    /**
     * 非流式请求
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Mono<T>
     */
    public static <T> Mono<T> monoChat(
            String url,
            String accessToken,
            Object request,
            Class<T> type) {

        WebClient client = WebClient.builder()
                .baseUrl(url + accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();

        return client.post()
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(type);
    }

    /**
     * 流式请求
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Flux<T>
     */
    public static <T> Flux<T> fluxChat(
            String url,
            String accessToken,
            Object request,
            Class<T> type) {

        WebClient client = WebClient.builder()
                .baseUrl(url + accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();

        return client.post()
                .body(BodyInserters.fromValue(request))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(type);
    }

}
