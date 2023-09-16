<div align="right">
<a href="/README.md">中文</a> &nbsp;|&nbsp;
<a href="/README_EN.md">EN</a>
</div>

<div align="center">

![gear-wenxinworkshop-starter](https://socialify.git.ci/gemingjia/gear-wenxinworkshop-starter/image?font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Light)

![LICENSE](https://img.shields.io/github/license/gemingjia/gear-wenxinworkshop-starter?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen.svg)
![JDK](https://img.shields.io/badge/JDK-17.0.5-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)

![COMMIT](https://img.shields.io/github/last-commit/gemingjia/gear-wenxinworkshop-starter?style=flat-square)
![LANG](https://img.shields.io/badge/language-Java-7F52FF?style=flat-square)

</div>
# Gear-WenXinWorkShop-Starter

## How to get access-token?

[Apply for WenxinYiyan & WenxinQianfan Big model API qualification, get access_token, and use SpringBoot to access WenxinYiyan API](https://juejin.cn/post/7260418945721991227)


1. Go to [WenXinYiYan qualification application](https://cloud.baidu.com/product/wenxinworkshop) 

2. [Fill out the questionnaire](https://cloud.baidu.com/survey/qianfan.html)，and wait for approval (it took me one and a half days)

3. After approval,enter the [console](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/overview/index)，click[Create Application](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/create)
4. Enter the left side [Application List](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/list)，copy`API Key` and `Secret Key`
5. Replace your `API Key` and `Secret Key` with [Key] in the link and visit the following address
 > https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=[API-Key]&client_secret=[Secret-Key]

## 📖 Project Introduction
- The spring-boot-starter of Baidu's **"Wenxin Qianfan WENXINWORKSHOP"** large model can help you quickly access Baidu's AI capabilities. You can call Baidu's Wenxin Qianfan large model with only one line of code.
- Complete docking with the official API documentation of WenxinQianfan.
- Support streaming back of conversations.
- Full API support for `ErnieBot`、`ERNIE-Bot-turbo`、`BLOOMZ-7B`、`Ernie-Bot-VilG`、`VisualGLM-6B`、`Llama-2`、`Linly-Chinese-LLaMA-2-7B`、`Linly-Chinese-LLaMA-2-13B`、`ChatGLM2-6B`、`RWKV-4-World`、`OpenLLaMA-7B`、`Falcon-7B`、`Dolly-12B`、`MPT-7B-Instruct`、`Stable-Diffusion-v1.5`、`RWKV-4-pile-14B`、`RWKV-5-World`、`RWKV-Raven-14B`、`Falcon-40B`、`MPT-30B-instruct`、`Flan-UL2`、`Cerebras-GPT-13B`、`Cerebras-GPT-6.7B`、`Pythia-12B`、`Pythia-6.9B`、`GPT-J-6B`、`GPT-NeoX-20B`、`OA-Pythia-12B-SFT-4`、`GPT4All-J`、`StableLM-Alpha-7B` 、 `StarCoder`、`Prompt Template`  models (single round conversation, continuous conversation, streaming return).
- Support formore models will be added in behind version.

## 🚀 Quick Start

[Project demo](https://github.com/gemingjia/springboot-wenxin-demo)

```text
This version almost refactoring the entire project, the path between the client and the parameter class has changed, there is a certain incompatibility with the previous version, the method has not changed, just re-guide the package.

"Bloomz7BClient" -> "BloomZ7BClient"

Except "ErnieBot" and "Prompt", the receiving parameter class of the other conversational models is unified as ChatBaseRequest, and the response class is ChatResponse
The receiving parameter class of the image generation model is unified as ChatImageRequest, the response class is ImageBaseRequest, and the content is base64 encoded image.
```

### 1、Add Dependencies
- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>gear-wenxinworkshop-starter</artifactId>
  <version>0.0.9</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:gear-wenxinworkshop-starter:0.0.9' 
}
```

### 2、Add access-token
- application.yml & application.yaml
  ```yaml
  gear:
    wenxin:
      access-token: xx.xxxxxxxxxx.xxxxxx.xxxxxxx.xxxxx-xxxx
  ```
- application.properties
  ```properties
  gear.wenxin.access-token=xx.xxxxxxxxxx.xxxxxx.xxxxxxx.xxxxx-xxxx
  ```

### 3、Invoke Example
```java
@RestController
public class ChatController {

  // 要调用的模型的客户端
  @Resource
  private ErnieBotClient ernieBotClient;

  // 单次对话
  @PostMapping("/chat")
  public Mono<ChatResponse> chatSingle(String msg) {
    return ernieBotClient.chatSingle(msg);
  }

  // 连续对话
  @PostMapping("/chats")
  public Mono<ChatResponse> chatCont(String msg) {
    String chatUID = "test-user-1001";
    return ernieBotClient.chatCont(msg, chatUID);
  }

  // 流式返回，单次对话
  @PostMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ChatResponse> chatSingleStream(String msg) {
    return ernieBotClient.chatSingleOfStream(msg);
  }

  // 流式返回，连续对话
  @PostMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ChatResponse> chatContStream(String msg, String msgUid) {
    return ernieBotClient.chatContOfStream(msg, msgUid);
  }

  // 模板对话
  @PostMapping("/prompt")
  public Mono<PromptResponse> chatSingle() {
    Map<String, String> map = new HashMap<>();
    map.put("article", "我看见过波澜壮阔的大海，玩赏过水平如镜的西湖，却从没看见过漓江这样的水。漓江的水真静啊，静得让你感觉不到它在流动。");
    map.put("number", "20");
    PromptRequest promptRequest = new PromptRequest();
    promptRequest.setId(1234);
    promptRequest.setParamMap(map);

    return promptBotClient.chatPrompt(promptRequest);
  }

}
```

## 📑Documentation

<div>
Click => 
<a href="/wenxin-doc-en.md">Documents</a>
</div>

## Open Source License
[LICENSE](https://www.apache.org/licenses/LICENSE-2.0)
