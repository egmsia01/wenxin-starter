package com.gearwenxin.common;

import com.gearwenxin.model.erniebot.ErnieResponse;
import com.google.gson.Gson;
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

    public static final Gson GSON = new Gson();

    /**
     * 非流式请求
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Mono<ErnieResponse>对象
     */
    public static <T> Mono<ErnieResponse> monoChat(String url, String accessToken, T request) {

        WebClient client = WebClient.builder()
                .baseUrl(url + accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();

        return client.post()
                .body(BodyInserters.fromValue(GSON.toJson(request)))
                .retrieve()
                .bodyToMono(ErnieResponse.class);
    }

    /**
     * 流式请求
     *
     * @param url         请求地址
     * @param accessToken accessToken
     * @param request     请求类
     * @return Mono<ErnieResponse>对象
     */
    public static <T> Flux<ErnieResponse> fluxChat(String url, String accessToken, T request) {

        WebClient client = WebClient.builder()
                .baseUrl(url + accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();

        return client.post()
                .body(BodyInserters.fromValue(GSON.toJson(request)))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(ErnieResponse.class)
                .map(ChatUtils::extractResultFromResponse);
    }

    private static ErnieResponse extractResultFromResponse(ErnieResponse response) {
        return response;
    }
}
