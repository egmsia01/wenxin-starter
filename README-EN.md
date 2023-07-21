<div align="right">
<a href="/README.md">中文</a> &nbsp;|&nbsp;
<a href="/README_en-US.md">EN</a>
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
1. Go to [WenXinYiYan qualification application](https://cloud.baidu.com/product/wenxinworkshop) 

2. [Fill out the questionnaire](https://cloud.baidu.com/survey/qianfan.html)，and wait for approval (it took me one and a half days)

3. After approval,enter the [console](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/overview/index)，click[Create Application](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/create)
4. Enter the left side [Application List](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/list)，copy`API Key` and `Secret Key`
5. Replace your `API Key` and `Secret Key` with [Key] in the link and visit the following address
 > https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=[API-Key]&client_secret=[Secret-Key]

## 📖 Project Introduction
- Use Baidu's ** "Wenxin Qianfan WENXINWORKSHOP"** large model spring-boot-starter to help you quickly access Baidu's AI capabilities with just one line of code to call Baidu Wenxin Qianfan large model.
- Baidu has opened ErnieBot, Ernie-Bot-Turbo, BLOOMZ-7B, Embedding-V1 models. This project is under rapid development and iteration, and more model support will be added later.
- It currently basically supports the API of the "Wenxin Yiyian ErnieBot" large model (single round dialogue, continuous dialogue).
- Version 0.0.2 will add support for streaming returns and Ernie-Bot-Turbo, BLOOMZ-7B, Embedding-V1 model support.


## 🚀 Quick Start
### 1、Add Dependencies
- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>gear-wenxinworkshop-starter</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:gear-wenxinworkshop-starter:0.0.1-SNAPSHOT' 
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
        ErnieResponse ernieResponse = ernieBotClient.chatWithSingleRound(msg);
        return ResultUtils.success(ernieResponse.getResult());
    }

    // Continuous chat
    @PostMapping("/chat")
    public BaseResponse<String> chat(String msg) {
        String chatUID = "test-user-1001";
        ErnieResponse ernieResponse = ernieBotClient.chatMultipleRounds(msg, chatUID);
        return ResultUtils.success(ernieResponse.getResult());
    }

}
```

## 📑Documentation
[~Owe first~](http://mopen.cloud/)

## Open Source License
[LICENSE](https://www.apache.org/licenses/LICENSE-2.0)
