package com.gearwenxin.common;

import com.gearwenxin.model.erniebot.ErnieResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.gearwenxin.common.CommonUtils.GSON;

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
                .bodyToMono(ErnieResponse.class)
                .map(ConvertUtils::convertFromResponse);
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
                .map(ConvertUtils::convertFromResponse);
    }


}
