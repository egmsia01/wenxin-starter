package com.gearwenxin.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptRequest {

    /**
     * 模板id
     */
    private Integer id;

    /**
     * 参数map
     */
    private Map<String, String> paramMap;

}
