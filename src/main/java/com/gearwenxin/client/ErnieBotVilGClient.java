package com.gearwenxin.client;

import com.gearwenxin.common.*;
import com.gearwenxin.exception.BusinessException;
import com.gearwenxin.model.Message;
import com.gearwenxin.model.chatmodel.ChatVilGCRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotVilGClient implements BaseBot, ImageBot {

    private String accessToken;
    private static final String TAG = "ErnieBotVilGClient_";
    public static final String PREFIX_MSG_HISTORY_MONO = "Mono_";
    public static final String PREFIX_MSG_HISTORY_FLUX = "Flux_";
    private static final String URL = URLConstant.ERNIE_BOT_TURBO_URL;

    // 每个模型的历史消息Map
    private static Map<String, Queue<Message>> TURBO_MESSAGES_HISTORY_MAP = new ConcurrentHashMap<>();

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    protected ErnieBotVilGClient() {
    }

    // 获取access-token
    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        return TURBO_MESSAGES_HISTORY_MAP;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        TURBO_MESSAGES_HISTORY_MAP = map;
    }

    @Override
    public String getURL() {
        return getCustomURL();
    }

    @Override
    public byte[] chatImage(ChatVilGCRequest chatVilGCRequest) {
        validChatVilGCRequest(chatVilGCRequest);

        log.info(TAG + "imageRequest => {}", chatVilGCRequest.toString());

        String base64Image = ChatUtils.monoPost(
                        getURL(),
                        getAccessToken(),
                        chatVilGCRequest,
                        String.class)
                .block();

        return Base64.decodeBase64(base64Image);
    }

    public void validChatVilGCRequest(ChatVilGCRequest chatVilGCRequest) {
        if (chatVilGCRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "chatVilGCRequest is null");
        }
        // 检查content不为空
        if (StringUtils.isEmpty(chatVilGCRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (chatVilGCRequest.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
        if (chatVilGCRequest.getWidth() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "width is less than or eq 0");
        }
        if (chatVilGCRequest.getHeight() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "height is less than or eq 0");
        }
    }


}
