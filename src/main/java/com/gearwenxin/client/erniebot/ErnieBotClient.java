package com.gearwenxin.client.erniebot;

import cn.hutool.http.HttpUtil;
import com.gearwenxin.common.RoleEnum;
import com.gearwenxin.common.URLConstant;
import com.gearwenxin.model.erniebot.ErnieResponse;
import com.gearwenxin.model.Message;
import com.google.gson.Gson;
import com.gearwenxin.model.erniebot.ErnieRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */

@Slf4j
@Service
public class ErnieBotClient implements ErnieBot {

    static Gson gson = new Gson();
    private final String accessToken;

    public ErnieBotClient(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public ErnieResponse chatWithSingleRound(ErnieRequest ernieRequest) {
        List<Message> messageList = new ArrayList<>();

        Message message = new Message();
        message.setRole(RoleEnum.user);
        message.setContent("请介绍一下你自己");
        messageList.add(message);
        ernieRequest.setMessages(messageList);

        String response = HttpUtil.post(URLConstant.ERNIE_BOT_URL + accessToken, gson.toJson(ernieRequest));

        ErnieResponse ernieResponse = gson.fromJson(response, ErnieResponse.class);
        return ernieResponse;
    }

    @Override
    public ErnieResponse chatWithSingleRound(String content) {
        List<Message> messageList = new ArrayList<>();
        ErnieRequest ernieRequest = new ErnieRequest();

        Message message = new Message();
        message.setRole(RoleEnum.user);
        message.setContent(content);
        messageList.add(message);

        ernieRequest.setMessages(messageList);

        String response = HttpUtil.post(URLConstant.ERNIE_BOT_URL + accessToken, gson.toJson(ernieRequest));

        ErnieResponse ernieResponse = gson.fromJson(response, ErnieResponse.class);

        return ernieResponse;
    }

    @Override
    public ErnieResponse chatMultipleRounds(ErnieRequest ernieRequest) {
        return null;
    }

    @Override
    public ErnieResponse chatMultipleRounds(String content) {
        return null;
    }


}
