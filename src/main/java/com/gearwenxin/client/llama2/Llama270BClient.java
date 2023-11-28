package com.gearwenxin.client.llama2;

import com.gearwenxin.client.base.FullClient;
import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import com.gearwenxin.entity.Message;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia

 */
@Slf4j
@Lazy
@Service
public class Llama270BClient extends FullClient {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "Llama2-70B-Client";

    private static final String URL = Constant.LLAMA2_70B_URL;
    private static Map<String, Deque<Message>> LLAMA2_70B_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public Map<String, Deque<Message>> getMessageHistoryMap() {
        return LLAMA2_70B_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Deque<Message>> map) {
        LLAMA2_70B_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return URL;
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
