package com.gearwenxin.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模型URL
 *
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
public interface Constant {

    /**
     * 最大单条内容长度
     */
    int MAX_CONTENT_LENGTH = 2000;

    /**
     * 最大所有内容总长度
     */
    int MAX_TOTAL_LENGTH = 2000;

    /**
     * 最大system长度
     */
    int MAX_SYSTEM_LENGTH = 1024;

    // 中断标志
    Map<String, Boolean> INTERRUPT_MAP = new ConcurrentHashMap<>();

    boolean BASIC_MODE = false;

    String CHECK = "check";

    String GET_ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s";
    String PROMPT_URL = "https://aip.baidubce.com/rest/2.0/wenxinworkshop/api/v1/template/info";

}
