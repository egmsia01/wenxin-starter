# 1. Introduction

Currently, the following models are supported: **ErnieBot**, **Ernie-Bot-Turbo**, **BLOOMZ-7B**, and **Prompt Template**.

# 2. Parameters and Return Values

## ErnieBot (ChatErnieRequest: ErnieBot Parameter Configuration)

| Variable Name | Type   | Description                                                  |
| ------------- | ------ | ------------------------------------------------------------ |
| userId        | String | Represents the unique identifier of the end-user, which can be used to monitor and detect abusive behavior, preventing malicious API calls. |
| content       | String | Chat text information. The length of a single `content` cannot exceed 2000 characters. In continuous conversations, if the total length of `content` exceeds 2000 characters, the system will gradually forget the earliest historical sessions until the total length of `content` is no more than 2000 characters. |
| temperature   | Float  | (1) A higher value will make the output more random, while a lower value will make it more focused and deterministic.<br>(2) Default: `0.95`, Range: `(0, 1.0]`, cannot be 0.<br>(3) It is recommended to set either this parameter or `top_p`, not both.<br>(4) It is recommended not to change both `top_p` and `temperature` simultaneously. |
| topP          | Float  | (1) Affects the diversity of the output text. The larger the value, the stronger the diversity of the generated text.<br>(2) Default: `0.8`, Range: `[0, 1.0]`<br>(3) It is recommended to set either this parameter or `temperature`, not both.<br>(4) It is recommended not to change both `top_p` and `temperature` simultaneously. |
| penaltyScore  | Float  | Increases the penalty for already generated `tokens` to reduce the repetition of generated text. Explanation:<br>(1) The larger the value, the greater the penalty.<br>(2) Default: `1.0`, Range: `[1.0, 2.0]`. |

## ChatResponse: Response Class for Regular Conversations

| Variable Name     | Type    | Description                                                  |
| ------------------ | ------- | ------------------------------------------------------------ |
| id                 | String  | ID of this round of conversation.                            |
| object             | String  | Response type, `chat.completion` for multi-turn conversation. |
| created            | Integer | Timestamp.                                                   |
| sentenceId         | Integer | Represents the sequence number of the current sentence. This field will only be returned in streaming interface mode. |
| isEnd              | Boolean | Indicates whether the current sentence is the last one. This field will only be returned in streaming interface mode. |
| isTruncated        | Boolean | Indicates whether the current generated result is truncated. |
| result             | String  | Dialogue return result.                                      |
| needClearHistory   | Boolean | Indicates whether the user input contains security issues and suggests closing the current session and clearing historical session information.<br>true: Yes, indicating that there are security risks in the user input. It is recommended to close the current session and clear historical session information.<br>false: No, indicating that there are no security risks in the user input. |
| usage              | Usage   | Token statistics information, token count = Chinese character count + word count_1.3 (only for estimation logic). |
| errorCode          | Integer | Error code, normal is `null`.                                |
| errorMsg           | String  | Error description information to help understand and resolve the error. Normal is `null`. |

## Usage: Token Usage

| Variable Name     | Type | Description         |
| ------------------ | ---- | -------------------- |
| promptTokens       | int  | Number of prompt tokens |
| completionTokens   | int  | Number of response tokens |
| totalTokens        | int  | Total number of tokens |



## Ernie-Bot-Turbo

ChatTurbo7BRequest: Parameter Configuration Class Shared by Ernie-Bot-Turbo and BLOOMZ-7B Models

| Variable Name | Type   | Description                                                  |
| ------------- | ------ | ------------------------------------------------------------ |
| userId        | String | Represents the unique identifier of the end-user, which can be used to monitor and detect abusive behavior, preventing malicious API calls. |
| content       | String | Chat text information. The length of a single `content` cannot exceed 2000 characters. In continuous conversations, if the total length of `content` exceeds 2000 characters, the system will gradually forget the earliest historical sessions until the total length of `content` is no more than 2000 characters. |

Response class `ChatResponse` is the same as for **ErnieBot**.



## BLOOMZ-7B

All parameters are the same as for **Ernie-Bot-Turbo**.



## Prompt Template

ChatPromptRequest: Parameter Configuration Class for Prompt Template

| Variable Name | Type                | Description                                                  |
| ------------- | ------------------- | ------------------------------------------------------------ |
| id            | int                 | Template ID corresponding to the prompt project              |
| paramMap      | Map<String, String> | Map of interpolation variable names and interpolation variables |

PromptResponse: Response Class for Prompt Template

| Name             | Type           | Description                                      |
| ---------------- | -------------- | ------------------------------------------------- |
| log_id           | String         | Unique `log id` for problem location            |
| result           | PromptResult   | Template content details                         |
| status           | Integer        | Status code, normal is 200                        |
| success          | Boolean        | Whether the call is successful, true for success |
| errorCode        | Integer        | Error code, normal is `null`.                    |
| errorMsg         | String         | Error message, normal is `null`.                 |
| promptErrCode    | String         | `Prompt` error code                              |
| promptErrMessage | PromptErrMessage | `Prompt` error message object                    |

PromptResult:

| Name                | Type   | Description                                 |
| ------------------- | ------ | -------------------------------------------- |
| templateId          | String | Template ID corresponding to the prompt project |
| templateName        | String | Template name                                |
| templateContent     | String | Original content of the template             |
| templateVariables   | String | Template variable interpolation             |
| content             | String | Template content after filling in variables  |

PromptErrMessage:

| Name   | Type   | Description     |
| ------ | ------ | --------------- |
| global | String | Error message description |



## Error Codes

Error codes and descriptions (source: [Official Documentation of 文心千帆](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/jlil56u11)):

| Error Code | Error Message                                                | Description                                                  |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1          | Unknown error                                                | Internal server error, please retry the request. If such errors persist, [submit a ticket](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279) in the Baidu Cloud Console to provide feedback. |
| 2          | Service temporarily unavailable                              | Service temporarily unavailable, please retry the request. If such errors persist, [submit a ticket](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279&questionId=1549) in the Baidu Cloud Console to provide feedback. |
| 3          | Unsupported openapi method                                   | The called API does not exist, please check and try again.   |
| 4          | Open api request limit reached                               | Cluster limit exceeded.                                      |
| 6          | No permission to access data                                 | No permission to access user data.                           |
| 13         | Get service token failed                                     | Failed to obtain token.                                      |
| 14         | IAM Certification failed                                     | IAM authentication failed.                                  |
| 15         | app not exists or create failed                              | The application does not exist or creation failed.          |
| 17         | Open api daily request limit reached                         | Daily request limit exceeded. For APIs that have been released, please directly enable billing in the console for unrestricted usage. For APIs that have not been released, [submit a ticket](https://ticket.bce.baidu.com/?_=1683861660309#/ticket/create?productId=279&questionId=1548) in the Baidu Cloud Console to provide feedback. |
| 18         | Open api qps request limit reached                           | QPS (queries per second) limit exceeded. For APIs that have been released, please directly enable billing in the console for unrestricted usage. For APIs that have not been released, [submit a ticket](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279&questionId=1548) in the Baidu Cloud Console to provide feedback. |
| 19         | Open api total request limit reached                         | Total request limit exceeded. For APIs that have been released, please directly enable billing in the console for unrestricted usage. For APIs that have not been released, [submit a ticket](https://ticket.bce.baidu.com/#/ticket/create?useticket=1&productId=279&questionId=1548) in the Baidu Cloud Console to provide feedback. |
| 100        | Invalid parameter                                            | Invalid `access_token` parameter, please check and try again. |
| 110        | Access token invalid or no longer valid                      | Invalid `access_token`.                                     |
| 111        | Access token expired                                         | Access token expired.                                       |
| 336000     | Internal error                                               | Internal service error.                                     |
| 336001     | Invalid Argument                                             | Invalid input format, such as missing required parameters.   |
| 336002     | Invalid JSON                                                 | Non-standard JSON format in the input body.                 |
| 336003     | {specific reason for invalid parameter validation} Explanation: Different parameters return different error messages. | Invalid parameter validation, the `error_msg` field will provide specific reasons. |
| 336004     | {specific reason for permission control error}               | Permission control error.                                   |
| 336005     | API name not exist                                           | Custom model service `apiname` does not exist.              |
| 336100     | Artificial intelligence is a revolutionary technology. There are too many interactions with me, please ask me again later. | Artificial intelligence is a revolutionary technology. There are too many interactions with me, please ask me again later. |
| 336101     | Invalid HTTP Method                                          | Invalid HTTP Method, only POST requests are currently supported. |



## Official Documentation Request Class Reference

**(The classes listed here are for information only. Developers do not need to concern themselves with these classes. For passing parameters, please refer to the `Chat.*.Request` classes.)**



ErnieRequest: Request class for the ErnieBot model.

| Variable Name | Type           | Description                                                  |
| ------------- | -------------- | ------------------------------------------------------------ |
| userId        | String         | Represents the unique identifier of the end-user, which can be used to monitor and detect abusive behavior, preventing malicious API calls. |
| messages      | Queue<Message> | Chat context information.<br>(1) `messages` member cannot be empty. One member represents a single-turn conversation, and multiple members represent multi-turn conversations.<br>(2) The last `message` represents the current request information, and the previous `message` represents historical conversation information.<br>(3) The number of members must be odd, and the `role` of the `message` in the members must be `user`, `assistant` alternately.<br>(4) The length of `content` of the last `message` (i.e., the question of this round of conversation) cannot exceed 2000 characters. If the total length of `content` in `messages` exceeds 2000 characters, the system will gradually forget the earliest historical sessions until the total length of `content` is no more than 2000 characters. |
| temperature   | Float          | (1) A higher value will make the output more random, while a lower value will make it more focused and deterministic.<br>(2) Default: `0.95`, Range: `(0, 1.0]`, cannot be 0.<br>(3) It is recommended to set either this parameter or `top_p`, not both.<br>(4) It is recommended not to change both `top_p` and `temperature` simultaneously. |
| topP          | Float          | (1) Affects the diversity of the output text. The larger the value, the stronger the diversity of the generated text.<br>(2) Default: `0.8`, Range: `[0, 1.0]`<br>(3) It is recommended to set either this parameter or `temperature`, not both.<br>(4) It is recommended not to change both `top_p` and `temperature` simultaneously. |
| penaltyScore  | Float          | Increases the penalty for already generated `tokens` to reduce the repetition of generated text. Explanation:<br>(1) The larger the value, the greater the penalty.<br>(2) Default: `1.0`, Range: `[1.0, 2.0]`. |
| stream        | Boolean        | Whether to return data in streaming interface format. Default is `false`. |



# 3. Built-in Methods and Examples

Starter provides four client models: 

| Model            | Client              |
| ----------------- | ------------------- |
| ErnieBot        | ErnieBotClient      |
| Ernie-Bot-Turbo | ErnieBotTurboClient |
| BLOOMZ-7B       | Bloomz7BClient      |
| Prompt Template | PromptClient        |



Here's an example using `ErnieBotClient`, and the `BaseResponse` is defined as follows:
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



## Single Conversation

### Non-streaming Response

```java
@RestController
public class ChatController {
 
    // Client model to call
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // Single conversation
    @PostMapping("/chat")
    public BaseResponse<String> chatSingle(String msg) {
        ChatResponse response = ernieBotClient.chatSingle(msg);
        return BaseResponse.success(response.getResult());
    }
 
}
```

### Streaming Response

```java
@RestController
public class ChatController {
 
    // Client model to call
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // Streaming response, single conversation
    @PostMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public BaseResponse<Flux<ChatResponse>> chatSingleStream(String msg) {
        Flux<ChatResponse> chatResponseFlux = ernieBotClient.chatSingleOfStream(msg);
        return BaseResponse.success(chatResponseFlux);
    }
 
}
```



## Continuous Conversation

Continuous conversation is internally built-in.

### Non-streaming Response

```java
@RestController
public class ChatController {
 
    // Client model to call
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // Continuous conversation, default parameters
    @PostMapping("/chats")
    public BaseResponse<String> chatCont(String msg) {
        String chatUID = "test-user-1001";
        ChatResponse response = ernieBotClient.chatCont(msg, chatUID);
        return BaseResponse.success(response.getResult());
    }
 
}
```

### Streaming Response

```java
@RestController
public class ChatController {
 
    // Client model to call
    @Resource
    private ErnieBotClient ernieBotClient;
 
    // Streaming response, continuous conversation
    @PostMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public BaseResponse<Flux<ChatResponse>> chatContStream(String msg, String msgUid) {
        Flux<ChatResponse> chatResponseFlux = ernieBotClient.chatContOfStream(msg, msgUid);
        return BaseResponse.success(chatResponseFlux);
    }
 
}
```

## Prompt Template

```java

@RestController
public class ChatController {

    // Client model to call
    @Resource
    private PromptClient promptClient;

    // Template conversation
    @PostMapping("/rompt")
    public BaseResponse<PromptResponse> chatSingle() {
        Map<String, String> map = new HashMap<>();
        map.put("article", "I have seen the magnificent sea and enjoyed the mirror-like West Lake, but I have never seen water like that of the Lijiang River.\n"
                +"The water of the Lijiang River is really calm, so calm that you can't feel it flowing.");
        map.put("number", "20");
        PromptRequest promptRequest = new PromptRequest();
        promptRequest.setId(1234);
        promptRequest.setParamMap(map);
        PromptResponse promptResponse = promptClient.chatPrompt(promptRequest);

        return BaseResponse.success(promptResponse);
    }

}
```



## Historical Message Record Operations

### Export Historical Message Records

This functionality is **Export Historical Message Records** for developers to save historical message records on their own. Default implementation is not provided, please implement it by yourself.

```java
@Service
public class ChatService {
 
    @Resource
    private ErnieBotClient ernieBotClient;
 
    public void exportMsg() {
        Map<String, Queue<Message>> messageHistoryMap = ernieBotTurboClient.getMessageHistoryMap();
    }
 
}
```

### Import Historical Message Records

```java
@Service
public class ChatService {
 
    @Resource
    private ErnieBotClient ernieBotClient;
 
    public void importMsg() {
        Map<String, Queue<Message>> messageHistoryMap = new HashMap<>();
        Message userMessage = WenXinUtils.buildUserMessage("Hello");
        Message assistantMessage = WenXinUtils.buildAssistantMessage("I am ErnieBot");
        Queue<Message> messagesQueue = WenXinUtils.buildMessageQueue(userMessage, assistantMessage);
        messageHistoryMap1.put("test-user1-1001", messagesQueue);
        ernieBotClient.initMessageHistoryMap(messageHistoryMap1);
    }
 
}
```



# 4. Differences Between Models

### ERNIE-Bot

ERNIE-Bot is a large language model developed by Baidu, covering a massive amount of Chinese data and possessing stronger capabilities in conversation, question answering, content generation, etc.

### ERNIE-Bot-Turbo

ERNIE-Bot-Turbo is a large language model developed by Baidu, covering a massive amount of Chinese data and possessing stronger capabilities in conversation, question answering, content generation, etc. It has faster response speed.

### BLOOMZ-7B

BLOOMZ-7B is a well-known large language model developed and open-sourced by BigScience. It can output text in 46 languages and 13 programming languages.

### Llama-2

#### Llama-2-7b-chat

Llama-2-7b-chat is developed and open-sourced by Meta AI and performs well in encoding, reasoning, and knowledge application scenarios. Llama-2-7b-chat is the high-performance version.

#### Llama-2-13b-chat

Llama-2-13b-chat is developed and open-sourced by Meta AI and performs well in encoding, reasoning, and knowledge application scenarios. Llama-2-13b-chat balances performance and effectiveness.

#### Llama-2-70b-chat

Llama-2-70b-chat is developed and open-sourced by Meta AI and performs well in encoding, reasoning, and knowledge application scenarios. Llama-2-70b-chat is the high-precision version.

### Embedding-V1

Embedding-V1 is a text representation model based on Baidu's Wenxin large model technology. It converts text into vector form represented by numbers, used for text retrieval, information recommendation, knowledge mining, etc.

### Prompt Template

Prompt Project is an AI technology that optimizes large models' input in terms of structure, content, etc., to better control the model's output. It limits the input of the large model to a specific range, thus better controlling the model's output.