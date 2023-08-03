package com.gearwenxin.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * @date 2023/7/26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptResult {

    /**
     * prompt工程里面对应的模板id
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板原始内容
     */
    private String templateContent;

    /**
     * 模板变量插值
     */
    private String templateVariables;

    /**
     * 将变量插值填充到模板原始内容后得到的模板内容
     */
    private String content;

}
