package com.gearwenxin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/11/1
 */
@Data
@Component
@ConfigurationProperties("gear.wenxin")
public class WenXinProperties {

    private String accessToken;
    private String apiKey;
    private String secretKey;

    private List<String> model_qps;

    private Integer saveScheduledTime;

    public List<String> getModelQPSList() {
        return model_qps;
    }

    public void setModelQPSList(List<String> model_qps) {
        this.model_qps = model_qps;
    }

}
