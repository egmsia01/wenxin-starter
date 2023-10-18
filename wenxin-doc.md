# 一、前言

目前支持 **ErnieBot**、**Ernie-Bot-Turbo**、**BLOOMZ-7B** 模型，以及 **Prompt模板 **。

# 二、参数与返回值

## ErnieBot（文心一言）

**ChatErnieRequest**：**ErnieBot **参数配置类

| 变量名       | 类型   | 说明                                                         |
| ------------ | ------ | ------------------------------------------------------------ |
| userId       | String | 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用 |
| content      | String | 聊天文本信息。单个`content` 长度不能超过2000个字符；连续对话中，若 `content` 总长度大于2000字符，系统会依次遗忘最早的历史会话，直到 `content` 的总长度不超过2000个字符。 |
| temperature  | Float  | (1) 较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定<br>(2) 默认 `0.95`，范围 `(0,1.0]`，不能为0<br>(3) 建议该参数和 `top_p` 只设置1个<br>(4) 建议 `top_p` 和 `temperature` 不要同时更改 |
| topP         | Float  | (1) 影响输出文本的多样性，取值越大，生成文本的多样性越强<br>(2) 默认`0.8`，取值范围 `[0,1.0]`<br>(3) 建议该参数和 `temperature` 只设置1个<br>(4) 建议 `top_p` 和 `temperature` 不要同时更改 |
| penaltyScore | Float  | 通过对已生成的 `token` 增加惩罚，减少重复生成的现象。说明:<br>(1) 值越大表示惩罚越大<br>(2) 默认 `1.0`，取值范围 `[1.0,2.0]` |

**ChatResponse**：普通对话的响应类

| 变量名           | 类型    | 说明                                                         |
| ---------------- | ------- | ------------------------------------------------------------ |
| id               | String  | 本轮对话的id                                                 |
| object           | String  | 回包类型，chat.completion为多轮对话返回                      |
| created          | Integer | 时间戳                                                       |
| sentenceId       | Integer | 表示当前子句的序号。只有在流式接口模式下会返回该字段         |
| isEnd            | Boolean | 表示当前子句是否是最后一句。只有在流式接口模式下会返回该字段 |
| isTruncated      | Boolean | 当前生成的结果是否被截断                                     |
| result           | String  | 对话返回结果                                                 |
| needClearHistory | Boolean | 表示用户输入是否存在安全，是否关闭当前会话，清理历史回话信息<br>true：是，表示用户输入存在安全风险，建议关闭当前会话，清理历史会话信息<br/>false：否，表示用户输入无安全风险 |
| usage            | Usage   | token统计信息，token数 = 汉字数+单词数_1.3 (仅为估算逻辑)    |
| errorCode        | Integer | 错误代码，正常为null                                         |
| errorMsg         | String  | 错误描述信息，帮助理解和解决发生的错误，正常为null           |

**Usage**：`tokens`使用情况

| 变量名           | 类型 | 说明         |
| ---------------- | ---- | ------------ |
| promptTokens     | int  | 问题tokens数 |
| completionTokens | int  | 回答tokens数 |
| totalTokens      | int  | tokens总数   |



## Ernie-Bot-Turbo

ChatTurbo7BRequest：Ernie-Bot-Turbo与BLOOMZ-7B模型共同的参数配置类

| 变量名  | 类型   | 说明                                                         |
| ------- | ------ | ------------------------------------------------------------ |
| userId  | String | 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用 |
| content | String | 聊天文本信息。单个`content` 长度不能超过2000个字符；连续对话中，若 `content` 总长度大于2000字符，系统会依次遗忘最早的历史会话，直到 `content` 的总长度不超过2000个字符。 |

响应类 `ChatResponse` ，同 **ErnieBot**。



## BLOOMZ-7B

所有参数同 **Ernie-Bot-Turbo** 。



## Prompt模板

**ChatPromptRequest**：**Prompt** 模板参数配置类

| 变量名   | 类型                | 说明                       |
| -------- | ------------------- | -------------------------- |
| id       | int                 | prompt工程里面对应的模板id |
| paramMap | Map<String, String> | Map<插值变量名1,插值变量>  |

**PromptResponse**：**Prompt** 模板响应类

| **名称**         | **类型**         | **描述**                      |
| ---------------- | ---------------- | ----------------------------- |
| log_id           | String           | 唯一的 `log id`，用于问题定位 |
| result           | PromptResult     | 模板内容详情                  |
| status           | Integer          | 状态码，正常200               |
| success          | Boolean          | 调用成功与否，成功为true      |
| errorCode        | Integer          | 错误代码,正常为null           |
| errorMsg         | String           | 错误信息,正常为null           |
| promptErrCode    | String           | `Prompt`错误代码              |
| promptErrMessage | PromptErrMessage | `Prompt`错误信息对象          |

**PromptResult**：

| **名称**          | **类型** | **描述**                                     |
| ----------------- | -------- | -------------------------------------------- |
| templateId        | String   | `prompt`工程里面对应的模板id                 |
| templateName      | String   | 模板名称                                     |
| templateContent   | String   | 模板原始内容                                 |
| templateVariables | String   | 模板变量插值                                 |
| content           | String   | 将变量插值填充到模板原始内容后得到的模板内容 |

**PromptErrMessage**：

| **名称** | **类型** | **描述**     |
| -------- | -------- | ------------ |
| global   | String   | 错误信息描述 |



## 错误码

errorCode错误码对应（来源：[文心千帆官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/jlil56u11)）：

| 错误码 | 错误信息                                                     | 描述                                                         |
| ------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1      | Unknown error                                                | 服务器内部错误，请再次请求， 如果持续出现此类错误，请在百度云控制台内[提交工单](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279)反馈 |
| 2      | Service temporarily unavailable                              | 服务暂不可用，请再次请求， 如果持续出现此类错误，请在百度云控制台内[提交工单](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279&questionId=1549)反馈 |
| 3      | Unsupported openapi method                                   | 调用的API不存在，请检查后重新尝试                            |
| 4      | Open api request limit reached                               | 集群超限额                                                   |
| 6      | No permission to access data                                 | 无权限访问该用户数据                                         |
| 13     | Get service token failed                                     | 获取token失败                                                |
| 14     | IAM Certification failed                                     | IAM鉴权失败                                                  |
| 15     | app not exists or create failed                              | 应用不存在或者创建失败                                       |
| 17     | Open api daily request limit reached                         | 每天请求量超限额，已上线计费的接口，请直接在控制台开通计费，调用量不受限制，按调用量阶梯计费；未上线计费的接口，请在百度云控制台内[提交工单](https://ticket.bce.baidu.com/?_=1683861660309#/ticket/create?productId=279&questionId=1548)反馈 |
| 18     | Open api qps request limit reached                           | QPS超限额，已上线计费的接口，请直接在控制台开通计费，调用量不受限制，按调用量阶梯计费；未上线计费的接口，请在百度云控制台内[提交工单](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279&questionId=1548)反馈 |
| 19     | Open api total request limit reached                         | 请求总量超限额，已上线计费的接口，请直接在控制台开通计费，调用量不受限制，按调用量阶梯计费；未上线计费的接口，请在百度云控制台内[提交工单](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279&questionId=1548)反馈 |
| 100    | Invalid parameter                                            | 无效的access_token参数，请检查后重新尝试                     |
| 110    | Access token invalid or no longer valid                      | access_token无效                                             |
| 111    | Access token expired                                         | access token过期                                             |
| 336000 | Internal error                                               | 服务内部错误                                                 |
| 336001 | Invalid Argument                                             | 入参格式有误，比如缺少必要参数                               |
| 336002 | Invalid JSON                                                 | 入参body不是标准的JSON格式                                   |
| 336003 | {参数校验不合法的具体原因}说明：参数不同，返回错误信息不同   | 参数校验不合法，error_msg中会给出具体原因                    |
| 336004 | {权限控制出错的具体原因}                                     | 权限控制出错                                                 |
| 336005 | API name not exist                                           | 定制化模型服务apiname不存在                                  |
| 336100 | 人工智能是一项革命性的技术。与我互动的人过多，请您稍后重新向我提问。 | 人工智能是一项革命性的技术。与我互动的人过多，请您稍后重新向我提问 |
| 336101 | Invalid HTTP Method                                          | 非法的HTTP Method， 当前仅支持POST请求                       |



## 官方文档请求类参考

**（这里的类仅为告知，开发者不必关心此类，传参请移步`Chat.*.Request`类）**



ErnieRequest：文心一言模型（ErnieBot）请求类。

| 变量名       | 类型           | 说明                                                         |
| ------------ | -------------- | ------------------------------------------------------------ |
| userId       | String         | 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用 |
| messages     | Deque<Message> | 聊天上下文信息。<br>(1) `messages` 成员不能为空，1个成员表示单轮对话，多个成员表示多轮对话<br>(2) 最后一个 `message` 为当前请求的信息，前面的 `message` 为历史对话信息<br>(3) 必须为奇数个成员，成员中 `message` 的 `role` 必须依次为 `user` 、`assistant`<br>(4) 最后一个 `message` 的 `content` 长度(即此轮对话的问题)不能超过2000个字符;如果 `messages` 中 `content` 总长度大于2000字符，系统会依次遗忘最早的历史会话，直到`content`的总长度不超过2000个字符 |
| temperature  | Float          | (1) 较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定<br>(2) 默认 `0.95`，范围 `(0,1.0]`，不能为0<br>(3) 建议该参数和 `top_p` 只设置1个<br>(4) 建议 `top_p` 和 `temperature` 不要同时更改 |
| topP         | Float          | (1) 影响输出文本的多样性，取值越大，生成文本的多样性越强<br>(2) 默认`0.8`，取值范围 `[0,1.0]`<br>(3) 建议该参数和 `temperature` 只设置1个<br>(4) 建议 `top_p` 和 `temperature` 不要同时更改 |
| penaltyScore | Float          | 通过对已生成的 `token` 增加惩罚，减少重复生成的现象。说明:<br>(1) 值越大表示惩罚越大<br>(2) 默认 `1.0`，取值范围 `[1.0,2.0]` |
| stream       | Boolean        | 是否以流式接口的形式返回数据，默认`false`                    |



# 三、内置方法与案例

Starter提供了四个模型的客户端，分别对应为

| 模型            | 客户端              |
| --------------- | ------------------- |
| ErnieBot        | ErnieBotClient      |
| Ernie-Bot-Turbo | ErnieBotTurboClient |
| BLOOMZ-7B       | Bloomz7BClient      |
| Prompt模板      | PromptClient        |



此处以 ErnieBotClient 为例，其中 BeseResponse 为
 ```java
 @Data
 public class BaseResponse<T> implements Serializable {
  
     private int code;
  
     private T data;
  
     private String message;
  
     public static <T> BaseResponse<T> success(T data) {
         BaseResponse<T> resp = new BaseResponse<>();
         resp.setData(data);
         resp.setCode(HttpStatus.OK.value());
         return resp;
     }
     
     public static <T> BaseResponse<T> error(int errorCode, String message) {
         BaseResponse<T> resp = new BaseResponse<>();
         resp.setCode(errorCode);
         resp.setMessage(message);
         return resp;
     }
  
 }
 ```



## 单次对话

### 非流式返回

```java
@RestController
public class ChatController {
 
    // 要调用的模型的客户端
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // 单次对话
    @PostMapping("/chat")
    public BaseResponse<String> chatSingle(String msg) {
        ChatResponse response = ernieBotClient.chatSingle(msg).block();
        return BaseResponse.success(response.getResult());
    }
 
}
```

### 流式返回

```java
@RestController
public class ChatController {
 
    // 要调用的模型的客户端
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // 流式返回，单次对话
    @GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatSingleStream(@RequestParam String msg) {
        Flux<ChatResponse> chatResponse = ernieBotClient.chatSingleOfStream(msg);

        return chatResponse.map(response -> "data: " + response.getResult() + "\n\n");
    }
 
}
```



## 连续对话

连续对话内部已内置

### 非流式返回

```java
@RestController
public class ChatController {
 
    // 要调用的模型的客户端
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // 连续对话，默认参数
    @PostMapping("/chats")
    public BaseResponse<String> chatCont(String msg) {
        String chatUID = "test-user-1001";
        ChatResponse response = ernieBotClient.chatCont(msg, chatUID).block();
        return BaseResponse.success(response.getResult());
    }
 
}
```

### 流式返回

```java
@RestController
public class ChatController {
 
    // 要调用的模型的客户端
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // 流式返回，连续对话
    @GetMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatContStream(@RequestParam String msg, @RequestParam String msgUid) {
        Flux<ChatResponse> chatResponse = ernieBotClient.chatContOfStream(msg, msgUid);

        return chatResponse.map(response -> "data: " + response.getResult() + "\n\n");
    }
 
}
```

## Prompt模板

```java

@RestController
public class ChatController {

    // 要调用的模型的客户端
    @Resource
    private PromptClient promptClient;

    // 模板对话
    @PostMapping("/rompt")
    public BaseResponse<PromptResponse> chatSingle() {
        Map<String, String> map = new HashMap<>();
        map.put("article", "我看见过波澜壮阔的大海，玩赏过水平如镜的西湖，却从没看见过漓江这样的水。\n"
                +"漓江的水真静啊，静得让你感觉不到它在流动。");
        map.put("number", "20");
        PromptRequest promptRequest = new PromptRequest();
        promptRequest.setId(1234);
        promptRequest.setParamMap(map);
        PromptResponse promptResponse = promptClient.chatPrompt(promptRequest).block();

        return BaseResponse.success(promptResponse);
    }

}
```



## 历史消息记录操作

### 导出历史消息记录

此功能为 **导出历史消息记录** ，供开发者自行保存历史消息记录，未提供默认实现，请开发者自行实现。

```java
@Service
public class ChatService {
 
    @Resource
    private ErnieBotClient ernieBotClient;
 
    public void exportMsg() {
        Map<String, Deque<Message>> messageHistoryMap = ernieBotTurboClient.getMessageHistoryMap();
    }
 
}
```

### 导入历史消息记录

```java
@Service
public class ChatService {
 
    @Resource
    private ErnieBotClient ernieBotClient;
 
    public void importMsg() {
        Map<String, Deque<Message>> messageHistoryMap = new HashMap<>();
        Message userMessage = WenXinUtils.buildUserMessage("你好");
        Message assistantMessage = WenXinUtils.buildAssistantMessage("我是文心一言");
        Deque<Message> messagesDeque = WenXinUtils.buildMessageDeque(userMessage, assistantMessage);
        messageHistoryMap1.put("test-user1-1001", messagesDeque);
        ernieBotClient.initMessageHistoryMap(messageHistoryMap1);
    }
 
}
```



# 四、模型之间的区别

### ERNIE-Bot

ERNIE-Bot是百度自行研发的大语言模型，覆盖海量中文数据，具有更强的对话问答、内容创作生成等能力。

### ERNIE-Bot-turbo

ERNIE-Bot-turbo是百度自行研发的大语言模型，覆盖海量中文数据，具有更强的对话问答、内容创作生成等能力，响应速度更快。

### BLOOMZ-7B

BLOOMZ-7B是业内知名的大语言模型，由BigScience研发并开源，能够以46种语言和13种编程语言输出文本。

### Llama-2

#### Llama-2-7b-chat

Llama-2-7b-chat由Meta AI研发并开源，在编码、推理及知识应用等场景表现优秀，Llama-2-7b-chat是高性能版本。

#### Llama-2-13b-chat

Llama-2-13b-chat是由Meta AI研发并开源，在编码、推理及知识应用等场景表现优秀，Llama-2-13b-chat是性能与效果均衡版本。

#### Llama-2-70b-chat

Llama-2-70b-chat由Meta AI研发并开源，在编码、推理及知识应用等场景表现优秀，Llama-2-70b-chat是高精度效果版本。

### Embedding-V1

Embedding-V1是基于百度文心大模型技术的文本表示模型，将文本转化为用数值表示的向量形式，用于文本检索、信息推荐、知识挖掘等场景。

### Prompt模板

Prompt工程指针对于Prompt进行结构、内容等维度进行优化的AI技术，它把大模型的输入限定在了一个特定的范围之中，进而更好地控制模型的输出。