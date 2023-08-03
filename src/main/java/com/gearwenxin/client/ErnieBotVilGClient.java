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

/**
 * @author Ge Mingjia
 * @date 2023/7/20
 */
@Slf4j
public abstract class ErnieBotVilGClient implements BaseBot, ImageBot {

    private String accessToken = null;
    private static final String TAG = "ErnieBotVilGClient_";

    // 最大的单个content字符数
    private static final int MAX_CONTENT_LENGTH = 2000;

    protected ErnieBotVilGClient() {
    }

    // 获取access-token
    protected abstract String getAccessToken();

    // 获取不固定的模型URL
    protected abstract String getCustomURL();

    @Override
    public String getCustomAccessToken() {
        return accessToken != null ? accessToken : getAccessToken();
    }

    @Override
    public void setCustomAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Map<String, Queue<Message>> getMessageHistoryMap() {
        log.warn(TAG + "ErnieBotVilGClient not have MessageHistoryMap");
        return null;
    }

    @Override
    public void initMessageHistoryMap(Map<String, Queue<Message>> map) {
        log.warn(TAG + "ErnieBotVilGClient not need init");
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
                        getCustomAccessToken(),
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
