package com.gearwenxin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/10/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionCall {

  /**
   * 触发的function名
   */
  private String name;

  /**
   * 请求参数
   */
  private String arguments;

  /**
   * 模型思考过程
   */
  private String thoughts;

}