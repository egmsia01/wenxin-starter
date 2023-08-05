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

### 1、Add Dependencies
- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>gear-wenxinworkshop-starter</artifactId>
  <version>0.0.5</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:gear-wenxinworkshop-starter:0.0.3.2' 
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

    // Model client to call
    @Resource
    private ErnieBotClient ernieBotClient;

    // Single round chat
    @PostMapping("/chat")
    public BaseResponse<String> chatSingle(String msg) {
        ChatResponse response = ernieBotClient.chatSingle(msg);
        return BaseResponse.success(response.getResult());
    }

    // Continuous chat
    @PostMapping("/chats")
    public BaseResponse<String> chatCont(String msg) {
        String chatUID = "test-user-1001";
        ChatResponse response = ernieBotClient.chatCont(msg, chatUID);
        return BaseResponse.success(response.getResult());
    }

    // Single round chat with stream
    @PostMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatSingleStream(String msg) {
        return ernieBotClient.chatSingleOfStream(msg);
    }

    //Continuous chat with stream
    @PostMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> chatContStream(String msg, String msgUid) {
        return ernieBotClient.chatContOfStream(msg, msgUid);
    }
    
    //prompt chat
    @PostMapping("/rompt")
    public BaseResponse<PromptResponse> chatSingle() {
        Map<String, String> map = new HashMap<>();
        map.put("article", "I have seen the magnificent sea and enjoyed the horizontal West Lake as a mirror, but I have never seen such water as the Li River. The water in the Li River is really quiet, so quiet that you can't feel it flowing.");
        map.put("number", "20");
        PromptRequest promptRequest = new PromptRequest();
        promptRequest.setId(1234);
        promptRequest.setParamMap(map);
        PromptResponse promptResponse = promptBotClient.chatPrompt(promptRequest);

        return BaseResponse.success(promptResponse);
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
