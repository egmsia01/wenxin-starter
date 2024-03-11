package com.gearwenxin.service;

import com.gearwenxin.common.Constant;
import com.gearwenxin.config.WenXinProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 */
@Slf4j
@Service
public class EmbeddingService {

    @Resource
    private WenXinProperties wenXinProperties;

    private String accessToken = null;
    private static final String TAG = "EmbeddingV1Client";

    private static final String URL = Constant.PROMPT_URL;

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

}
