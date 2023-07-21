package com.gearwenxin.common;

import cn.hutool.http.HttpUtil;

/**
 * @author Ge Mingjia
 * @date 2023/7/21
 */
public class ChatUtils {

    public static String commonChat(String url, String accessToken, String jsonBody) {

        return HttpUtil.post(URLConstant.ERNIE_BOT_URL + accessToken, jsonBody);
    }
}
