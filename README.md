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

## 获取access-token方法
1. 前往 [文心一言资格申请](https://cloud.baidu.com/product/wenxinworkshop) 

2. [填写问卷](https://cloud.baidu.com/survey/qianfan.html)，等待审核通过（我用了一天半）

3. 审核通过后进入 [控制台](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/overview/index)，点击[创建应用](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/create)
4. 进入左侧 [应用列表](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/list)，复制 `API Key` 与 `Secret Key`
5. 将你的 `API Key` 与 `Secret Key` 替换链接的[Key]，访问以下地址
 > https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=[API-Key]&client_secret=[Secret-Key]

## 📖 项目简介
- 百度 **“文心千帆 WENXINWORKSHOP”** 大模型的spring-boot-starter，可以帮助您快速接入百度的AI能力，只需一行代码即可调用百度文心千帆大模型。
- 目前已百度已开放ErnieBot、Ernie-Bot-Turbo、BLOOMZ-7B、Embedding-V1模型，此项目正在快速开发迭代中，后续将添加更多模型支持。
- 目前已基本支持“文心一言 ErnieBot”大模型的API（单轮对话、连续对话）。
- 0.0.2版本中将增加支持流式返回以及Ernie-Bot-Turbo、BLOOMZ-7B、Embedding-V1模型的支持。


## 🚀 快速开始
### 1、添加依赖
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

### 2、添加access-token
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

### 3、调用示例
```java
@RestController
public class ChatController {

    // 要调用的模型的客户端
    @Resource
    private ErnieBotClient ernieBotClient;

    // 单次对话
    @PostMapping("/chat")
    public BaseResponse<String> chatSingle(String msg) {
        ErnieResponse ernieResponse = ernieBotClient.chatWithSingleRound(msg);
        return ResultUtils.success(ernieResponse.getResult());
    }

    // 连续对话
    @PostMapping("/chat")
    public BaseResponse<String> chat(String msg) {
        String chatUID = "test-user-1001";
        ErnieResponse ernieResponse = ernieBotClient.chatMultipleRounds(msg, chatUID);
        return ResultUtils.success(ernieResponse.getResult());
    }

}
```

## 📑使用文档
[~先欠着~](http://mopen.cloud/)

## 开源协议
[LICENSE](https://www.apache.org/licenses/LICENSE-2.0)
