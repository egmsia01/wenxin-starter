package com.gearwenxin.client.ernie;

import com.gearwenxin.common.*;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.Message;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Ge Mingjia

 */
@Slf4j
@Service
public class ErnieBotTurboClient extends ErnieBotClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "ErnieBotTurboClient";
    private static final String URL = Constant.ERNIE_BOT_TURBO_URL;

    private static Map<String, Deque<Message>> TURBO_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return TURBO_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        TURBO_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public String getTag() {
        return TAG;
    }

}
