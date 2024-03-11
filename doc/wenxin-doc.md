# 一、前言

### 1、添加依赖

- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>wenxin-starter</artifactId>
  <version>2.0.0-beta</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:wenxin-starter:2.0.0-beta' 
}
```

### 2、添加access-token
- application.yml & application.yaml
  ```yaml
  gear:
    wenxin:
      access-token: xx.xxxxxxxxxx.xxxxxx.xxxxxxx.xxxxx-xxxx
  -------------或-----------------
  # 推荐
  gear:
    wenxin:
      api-key: xxxxxxxxxxxxxxxxxxx
      secret-key: xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  ```
- application.properties
  ```properties
  gear.wenxin.access-token=xx.xxxxxxxxxx.xxxxxx.xxxxxxx.xxxxx-xxxx
  ```

- 模型qps设置
  ```yaml
  gear:
    wenxin:
      model-qps:
        # 模型名 QPS数量
        - Ernie 10
        - Lamma 10
        - ChatGLM 10
  ```

# 二、参数与返回值

## ErnieBot（文心一言）

ErnieBot参数建议参考 [官方文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/clntwmv7t)

**ChatErnieRequest**：**ErnieBot、Ernie4Bot、ErnieBotTurbo** 参数配置类

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

## 文生图

详见下方使用示例。



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


## 配置类，创建对应的Bean
```java
@Configuration
public class ClientConfig {

  @Bean
  // 对应的模型名称，建议与modelConfig.setModelName("Ernie")保持一致 （允许不一致）
  @Qualifier("Ernie")
  public ChatClient ernieClient() {
    ModelConfig modelConfig = new ModelConfig();
    // 模型名称，必须跟设置的QPS数值的名称一致 (强烈建议与官网名称一致)
    modelConfig.setModelName("Ernie");
    // 模型url
    modelConfig.setModelUrl("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions");
    // 单独设置某个模型的access-token, 优先级高于全局access-token, 统一使用全局的话可以不设置
    modelConfig.setAccessToken("xx.xx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

    // 请求头，可选，不设置则使用默认值
    ModelHeader modelHeader = new ModelHeader();
    // 一分钟内允许的最大请求次数
    modelHeader.set_X_Ratelimit_Limit_Requests(100);
    // 一分钟内允许的最大tokens消耗，包含输入tokens和输出tokens
    modelHeader.set_X_Ratelimit_Limit_Tokens(2000);
    // 达到RPM速率限制前，剩余可发送的请求数配额，如果配额用完，将会在0-60s后刷新
    modelHeader.set_X_Ratelimit_Remaining_Requests(1000);
    // 达到TPM速率限制前，剩余可消耗的tokens数配额，如果配额用完，将会在0-60s后刷新
    modelHeader.set_X_Ratelimit_Remaining_Tokens(5000);

    modelConfig.setModelHeader(modelHeader);

    return new ChatClient(modelConfig);
  }

}
```
---
 以下均以 `Webflux` 为例，`Spring mvc` 请自行调整

## 单次对话

### 非流式返回

```java
@RestController
public class ChatController {

    // 要调用的模型的客户端（示例为文心）
    @Resource
    // 与上方配置类中的 @Qualifier("Ernie") 保持一致
    @Qualifier("Ernie")
    private ChatClient chatClient;

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> chatSingle(@RequestParam String msg) {
        // 单次对话 chatClient.chat(msg)
        Mono<ChatResponse> response = chatClient.chats(msg);
        return response.map(ChatResponse::getResult);
    }
 
}
```

### 流式返回

```java
@RestController
public class ChatController {

    // 要调用的模型的客户端（示例为文心）
    @Resource
    @Qualifier("Ernie")
    private ChatClient chatClient;

    @GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatSingleStream(@RequestParam String msg) {
        // 单次对话 chatClient.chatStream(msg)
        Flux<ChatResponse> responseFlux = chatClient.chatsStream(msg);
        return responseFlux.map(ChatResponse::getResult);
    }
 
}
```

## 连续对话

连续对话记录内部已内置

### 非流式返回

```java
@RestController
public class ChatController {

    // 要调用的模型的客户端（示例为文心）
    @Resource
    @Qualifier("Ernie")
    private ChatClient chatClient;

    @GetMapping(value = "/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> chatSingle(@RequestParam String msg, @RequestParam String uid) {
        // 单次对话 chatClient.chat(msg)
        Mono<ChatResponse> response = chatClient.chats(msg, uid);
        return response.map(ChatResponse::getResult);
    }
 
}
```

### 流式返回

```java
@RestController
public class ChatController {

    // 要调用的模型的客户端（示例为文心）
    @Resource
    @Qualifier("Ernie")
    private ChatClient chatClient;

    @GetMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatSingleStream(@RequestParam String msg, @RequestParam String uid) {
        // 单次对话 chatClient.chatStream(msg)
        Flux<ChatResponse> responseFlux = chatClient.chatsStream(msg, uid);
        return responseFlux.map(ChatResponse::getResult);
    }

}
```

## 文生图（Stable-Diffusion-XL） // 2.0.0-beta版本暂未支持

```java

// 文生图响应类
public class ImageResponse {
    /**
     * 请求的ID。
     */
    private String id;

    /**
     * 回包类型。固定值为 "image"，表示图像生成返回。
     */
    private String object;

    /**
     * 时间戳，表示生成响应的时间。
     */
    private int created;

    /**
     * 生成图片结果列表。
     */
    private List<ImageData> data;

    /**
     * token统计信息，token数 = 汉字数 + 单词数 * 1.3 （仅为估算逻辑）。
     */
    private Usage usage;

    /**
     * 错误代码，正常为 null
     */
    private Integer errorCode;

    /**
     * 错误信息，正常为 null
     */
    private String errorMsg;
}

public class ImageData {

    /**
     * 固定值 "image"，表示图像。
     */
    private String object;

    /**
     * 图片base64编码内容。
     */
    private String b64Image;

    /**
     * 图片序号。
     */
    private int index;
}
```


## Prompt模板 // 2.0.0-beta版本暂未支持

```java

```

## 历史消息记录操作

### 导出历史消息记录

此功能为 **导出历史消息记录** ，供开发者自行保存历史消息记录。

```java
@Service
public class ChatService {

  @Resource
  private WinXinActions winXinActions;

  // 导出指定msgId的消息（json）
  public String exportMessages(String msgId) {
    return winXinActions.exportMessages(msgId);
  }

  // 导出所有消息（json）
  public String exportAllMessages() {
    return winXinActions.exportAllMessages();
  }
 
}
```

### 导入历史消息记录

```java
@Service
public class ChatService {

  @Resource
  private WinXinActions winXinActions;

  // 初始化所有消息map
  public void initMessageMap(Map<String, Deque<Message>> map) {
    winXinActions.initMessageMap(map);
  }

  // 初始化指定msgId的消息
  public void initMessages(String msgId, Deque<Message> messageDeque) {
    winXinActions.initMessages(msgId, messageDeque);
  }

}
```