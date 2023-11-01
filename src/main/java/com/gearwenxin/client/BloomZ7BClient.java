package com.gearwenxin.client;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.Message;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/24
 */
@Service
public class BloomZ7BClient extends FullClient {

    @Resource
    private WenXinProperties wenXinProperties;

    public BloomZ7BClient() {
    }

    private String accessToken = null;
    private static final String TAG = "BloomZ-7B-Client";
    private static Map<String, Deque<Message>> BLOOMZ_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();
    private static final String URL = Constant.BLOOMZ_7B_URL;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return BLOOMZ_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        BLOOMZ_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
