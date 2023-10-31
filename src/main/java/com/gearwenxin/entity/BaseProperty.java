package com.gearwenxin.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Ge Mingjia
 */
@Data
@Builder
public class BaseProperty {

    private String url;

    private String tag;

    private String accessToken;

}
