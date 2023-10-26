<div align="right">
<a href="/README.md">中文</a> &nbsp;|&nbsp;
<a href="/README-EN.md">EN</a>
</div>

<div align="center">

![gear-wenxinworkshop-starter](https://socialify.git.ci/gemingjia/gear-wenxinworkshop-starter/image?font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Light)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen.svg)
![JDK](https://img.shields.io/badge/JDK-17.0.5-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)

![LICENSE](https://img.shields.io/github/license/gemingjia/gear-wenxinworkshop-starter?style=flat-square)
![COMMIT](https://img.shields.io/github/last-commit/gemingjia/gear-wenxinworkshop-starter?style=flat-square)
![LANG](https://img.shields.io/badge/language-Java-7F52FF?style=flat-square)

</div>
# Gear-WenXinWorkShop-Starter

## 获取access-token

1. 查看下面文章：
[文心一言获取access_token，并接入文心一言API](https://juejin.cn/post/7260418945721991227)
2. 直接配置`api_key`、`secret_key`，starter支持自动获取`access_token`。
---

## 项目简介
- 百度 **“文心千帆 WENXINWORKSHOP”** 大模型的spring-boot-starter，可以帮助您快速接入百度的AI能力。
- 完整对接文心千帆的官方API文档。
- 支持文生图。
- 内置对话记忆。
- 支持对话的流式返回。
- 完整支持文心千帆官网所有模型API，包括  `文心一言 ErnieBot`、`ERNIE-Bot-turbo`、`BLOOMZ-7B`、`Ernie-Bot-VilG`、`VisualGLM-6B`、`Llama-2`、`Linly-Chinese-LLaMA-2-7B`、`Linly-Chinese-LLaMA-2-13B`、`ChatGLM2-6B`、`RWKV-4-World`、`OpenLLaMA-7B`、`Falcon-7B`、`Dolly-12B`、`MPT-7B-Instruct`、`Stable-Diffusion-v1.5`、`RWKV-4-pile-14B`、`RWKV-5-World`、`RWKV-Raven-14B`、`Falcon-40B`、`MPT-30B-instruct`、`Flan-UL2`、`Cerebras-GPT-13B`、`Cerebras-GPT-6.7B`、`Pythia-12B`、`Pythia-6.9B`、`GPT-J-6B`、`GPT-NeoX-20B`、`OA-Pythia-12B-SFT-4`、`GPT4All-J`、`StableLM-Alpha-7B` 、 `StarCoder`、`Prompt模板`模型的API（单轮对话、连续对话、流式返回、图片生成）。
- 即将增加插件支持。

## 快速开始

[使用demo](https://github.com/gemingjia/springboot-wenxin-demo)

```text
0.0.9.1版本更改了历史消息的数据结构，由Queue更换为Deque，如有使用消息导入导出功能请注意修改，Deque兼容Queue的方法，您只需全局替换“Queue”为“Deque”即可，其余无需做任何修改，很抱歉给您带来不便。


除"ErnieBot"与"Prompt"外，其余的对话型模型接收参数类统一为 ChatBaseRequest，响应类为 ChatResponse
图片生成型模型接收参数类统一为 ChatImageRequest，响应类为 ImageResponse，内容为base64编码的图片。
```

### 1、添加依赖

- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>gear-wenxinworkshop-starter</artifactId>
  <version>0.0.9.7</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:gear-wenxinworkshop-starter:0.0.9.7' 
}
```

### 2、添加access-token
- application.yml & application.yaml
  ```yaml
  gear:
    wenxin:
      access-token: xx.xxxxxxxxxx.xxxxxx.xxxxxxx.xxxxx-xxxx
  -------------或-----------------
  gear:
    wenxin:
      api-key: xxxxxxxxxxxxxxxxxxx
      secret-key: xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  ```
- application.properties
  ```properties
  gear.wenxin.access-token=xx.xxxxxxxxxx.xxxxxx.xxxxxxx.xxxxx-xxxx
  ```

### 3、调用示例

```java

@RestController
public class ChatController {

  // 要调用的模型的客户端（示例为文心4.0）
  @Resource
  private ErnieBot4Client ernieBot4Client;
  
  // 单次对话
  @PostMapping("/chat")
  public Mono<ChatResponse> chatSingle(String msg) {
    return ernieBot4Client.chatSingle(msg);
  }

  // 连续对话
  @PostMapping("/chats")
  public Mono<ChatResponse> chatCont(String msg) {
    String chatUID = "test-user-1001";
    return ernieBot4Client.chatCont(msg, chatUID);
  }

  // 流式返回，单次对话
  @GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> chatSingleStream(@RequestParam String msg) {
    Flux<ChatResponse> chatResponse = ernieBot4Client.chatSingleOfStream(msg);

    return chatResponse.map(response -> "data: " + response.getResult() + "\n\n");
  }

  // 流式返回，连续对话
  @GetMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> chatContStream(@RequestParam String msg, @RequestParam String msgUid) {
    Flux<ChatResponse> chatResponse = ernieBot4Client.chatContOfStream(msg, msgUid);

    return chatResponse.map(response -> "data: " + response.getResult() + "\n\n");
  }

  // Prompt模板
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

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=gemingjia/gear-wenxinworkshop-starter&type=Date)](https://star-history.com/#gemingjia/gear-wenxinworkshop-starter)

## 更新日志

v0.0.9.7 - pre release
- 新增 对function call的简单支持 
- 新增 更细化的错误处理 
- 修复 ban_round类型错误导致的反序列化错误 #15。 
- 修复 system字段无长度限制 #16 
- 修复 system字段包含敏感词导致的npe问题 
- 修复 概率出现的The connection observed an error错误

v0.0.9.5 - Canary
- 支持文心4.0
- 同步官网响应字段（2023-10-20）。

v0.0.9.1
- 完全的响应式风格。
- 修复 快速连续请求导致的npe问题 ( #8 )。
- 修复 响应前再次请求导致的消息格式错误( #10 )。
- 修复 api_key配置错误导致的异常。
- 优化 非空校验。
- 优化 消息队列的数据结构。
- 优化 流式返回性能。
- 同步官网响应字段（2023-9-13）。

v0.0.7.2

全面转向webflux响应式风格，请注意适配返回值
- 重构 完全适配webflux响应式风格，去除代码中的阻塞操作。
- 优化 接口命名
- 优化 空值检测
- 优化 日志输出的规范性
- 优化 不同模型的URL配置名称（无感知）
- 新增 配置API-Key 和 Secret-Key自动获取access-Token，每次启动自动获取
- 新增 9+模型的API支持

v0.0.6

！此版本与之前版本客户端路径不兼容，请重新导包
- 重构 客户端的实现方式，大幅增加了拓展性
- 修复 URL错乱问题
- 优化 导入/导出历史消息记录方法
- 新增 access-token的设置方法，优先级 setCustomAccessToken > extend and override > application.yaml
- 新增 支持图片生成
- 新增 通用客户端CommonClient，新模型未适配时可使用这个，大概率可用
- 新增 高度自定义设计，直接extends DefaltParamsClient并重写方法即可
- 新增 大量模型适配，支持文心千帆所有模型，包括：文心一言 ErnieBot、ERNIE-Bot-turbo、BLOOMZ-7B、Ernie-Bot-VilG、VisualGLM-6B、Llama-2、Linly-Chinese-LLaMA-2-7B、Linly-Chinese-LLaMA-2-13B、ChatGLM2-6B、RWKV-4-World、OpenLLaMA-7B、Falcon-7B、Dolly-12B、MPT-7B-Instruct、Stable-Diffusion-v1.5、RWKV-4-pile-14B、RWKV-5-World、RWKV-Raven-14B、Falcon-40B、MPT-30B-instruct、Flan-UL2、Cerebras-GPT-13B、Cerebras-GPT-6.7B、Pythia-12B、Pythia-6.9B、GPT-J-6B、GPT-NeoX-20B、OA-Pythia-12B-SFT-4、GPT4All-J、StableLM-Alpha-7B 、 StarCoder、Prompt模板。

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
