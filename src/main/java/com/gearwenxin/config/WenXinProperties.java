package com.gearwenxin.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/11/1
 */
@Component
@ConfigurationProperties("gear.wenxin")
public class WenXinProperties {

    @Getter
    @Setter
    private String accessToken;

    @Getter
    @Setter
    private String apiKey;

    @Getter
    @Setter
    private String secretKey;

    @Getter
    @Setter
    private List<String> model_qps;

    @Getter
    @Setter
    private Integer saveScheduledTime;

    private boolean basicMode;

    public List<String> getModelQPSList() {
        return model_qps;
    }

    public void setModelQPSList(List<String> model_qps) {
        this.model_qps = model_qps;
    }

    private boolean getBasicMode() {
        return basicMode;
    }

    private void setBasicMode(boolean basicMode) {
        this.basicMode = basicMode;

    }

}
