package com.gearwenxin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/10/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionResponses {
    private String type;
    private Map<String, Map<String, Object>> properties;
}
