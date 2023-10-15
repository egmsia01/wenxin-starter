package com.gearwenxin.entity;

import com.gearwenxin.entity.enums.Role;

/**
 * @author Ge Mingjia
 * @date 2023/10/15
 */
public class Example {

  /**
   * 当前支持以下: 
   * user: 表示用户
   * assistant: 表示对话助手 
   * function: 表示函数
   */
  private Role role;

  /**
   * 对话内容,当前message存在function_call时可以为空,其他场景不能为空
   */
  private String content;

  /**
   * message作者;当role=function时,必填,且是响应内容中function_call中的name
   */  
  private String name;

  /**
   * 函数调用,function call场景下第一轮对话的返回,第二轮对话作为历史信息在message中传入
   */
  private FunctionCall functionCall;

}