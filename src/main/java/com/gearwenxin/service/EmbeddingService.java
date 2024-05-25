package com.gearwenxin.service;

import com.gearwenxin.config.WenXinProperties;
import javax.annotation.Resource;
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

    private String getAccessToken() {
        return wenXinProperties.getAccessToken();
    }

}
