package com.gearwenxin.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class V1Request {

    /**
     * 内容
     */
    private String content;

}
