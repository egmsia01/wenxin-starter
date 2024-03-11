<div align="right">
<a href="/README.md">中文</a> &nbsp;|&nbsp;
<a href="/README-EN.md">EN</a>
</div>

<div align="center">

![gear-wenxinworkshop-starter](https://socialify.git.ci/GMerge01/wenxin-starter/image?font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Light)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.2-brightgreen.svg)
![JDK](https://img.shields.io/badge/JDK-17.0.5-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)

![LICENSE](https://img.shields.io/github/license/GMerge01/wenxin-starter?style=flat-square)
![COMMIT](https://img.shields.io/github/last-commit/GMerge01/wenxin-starter?style=flat-square)
![LANG](https://img.shields.io/badge/language-Java-7F52FF?style=flat-square)

</div>
# WenXin-Starter

# [1.0版本链接](https://github.com/egmsia01/wenxin-starter/tree/master?tab=readme-ov-file)

## 项目简介
- 百度 **“文心千帆 WENXINWORKSHOP”** 大模型的spring-boot-starter，可以帮助您快速接入百度的AI能力。
- 完整对接文心千帆的官方API文档。
- 支持文生图。
- 内置对话记忆。
- 支持对话的流式返回。
- 支持文心千帆官网所有模型API，包括  `文心一言 ErnieBot`、`ERNIE-Bot-turbo`、`BLOOMZ-7B`、`Prompt模板`等模型的API（单轮对话、连续对话、流式返回、图片生成）。
- 即将增加插件支持。

## 快速开始

[使用demo](https://github.com/gemingjia/springboot-wenxin-demo)

```text
Client类型 -> 参数类 -> 响应类：

ErnieBotClient、ErnieBot4Client、ErnieBotTurboClient -> ChatErnieRequest -> ChatResponse
其他对话Client -> ChatBaseRequest -> ChatResponse
PromptBotClient -> ChatPromptRequest -> PromptResponse
文生图类Client -> ImageBaseRequest -> ImageResponse

详见文档或文心一言官方文档。
```

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

### 3、调用示例

```java

@Configuration
public class ClientConfig {

  @Bean
  @Qualifier("Ernie")
  public ChatClient ernieClient() {
    ModelConfig modelConfig = new ModelConfig();
    modelConfig.setModelName("Ernie");
    modelConfig.setModelUrl("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions");
    return new ChatClient(modelConfig);
  }

}

@RestController
public class ChatController {

  // 要调用的模型的客户端（示例为文心）
  @Resource
  @Qualifier("Ernie")
  private ChatClient chatClient;

  /**
   * chatClient.chatStream(msg) 单轮流式对话
   * chatClient.chatStream(new ChatErnieRequest()) 单轮流式对话, 参数可调
   * chatClient.chatsStream(msg, msgId) 连续对话
   * chatClient.chatsStream(new ChatErnieRequest(), msgId) 连续对话, 参数可调
   */
  
  // 单轮对话，流式接口
  @GetMapping(value = "/stream/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter chatSingleSSE(@RequestParam String msg) {
    SseEmitter emitter = new SseEmitter();
    chatClient.chatStream(msg).subscribe(response -> {
      try {
        emitter.send(SseEmitter.event().data(response.getResult()));
      } catch (Exception e) {
        emitter.completeWithError(e);
      }
    }, emitter::completeWithError, emitter::complete);
    return emitter;
  }

}
```

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=egmsia01/wenxin-starter&type=Date)](https://star-history.com/#egmsia01/wenxin-starter)

## 更新日志
v2.0.0 - bata

！ 2.x 版本与 1.x 版本不兼容
- 重构 SDK架构，大幅提升性能
- 重构 客户端生成方式，支持自定义多模型，不再需要适配
- 完善 普通chat接口现已可用

## 使用文档

<div>
点击跳转 => 
<a href="/wenxin-doc.md">使用文档</a>
</div>

## 开源协议
```text
MIT License

Copyright (c) 2023 GMerge

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
