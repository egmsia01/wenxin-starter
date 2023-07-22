package com.gearwenxin.common;

import com.gearwenxin.model.erniebot.ErnieResponse;
import com.google.gson.Gson;
import org.springframework.http.HttpHeaders;
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

    public static <T> Mono<ErnieResponse> monoChat(String url, String accessToken, T request) {

        WebClient client = WebClient.builder()
                .baseUrl(url + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.post()
                .body(BodyInserters.fromValue(GSON.toJson(request)))
                .retrieve()
                .bodyToMono(ErnieResponse.class);
    }

    public static <T> Flux<ErnieResponse> fluxChat(String url, String accessToken, T request) {

        WebClient client = WebClient.builder()
                .baseUrl(url + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.post()
                .body(BodyInserters.fromValue(GSON.toJson(request)))
                .retrieve()
                .bodyToFlux(ErnieResponse.class);
    }
}
