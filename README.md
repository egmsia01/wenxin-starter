<div align="right">
<a href="/README.md">中文</a> &nbsp;|&nbsp;
<a href="/README-EN.md">EN</a>
</div>

<div align="center">

![llms–nexus](https://socialify.git.ci/rainveil/llms–nexus/image?font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pattern=Floating%20Cogs&pulls=1&stargazers=1&theme=Light)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)
![JDK](https://img.shields.io/badge/JDK-17.0.5-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)

![LICENSE](https://img.shields.io/github/license/rainveil/llms–nexus?style=flat-square)
![COMMIT](https://img.shields.io/github/last-commit/rainveil/llms–nexus?style=flat-square)
![LANG](https://img.shields.io/badge/language-Java-7F52FF?style=flat-square)

</div>
# WenXin-Starter

# 📢 关于这个项目接下来的一些打算

各位开发者朋友们，大家好！我是这个项目的作者（或许也是唯一的维护者）。过去的一段时间，因为各种原因难以抽身，这个项目已经许久没有实质性的更新了。

而事实上，我也不知道应该如何进行下一步的更新。随着国内各家大模型技术的相互追逐与发展，文心系列的模型已不再是国产商用的唯一选择（尽管文心系列模型依旧拥有着一些优势）。而此项目是以百度的文心系列模型为起点做的，并且命名为"wenxin-starter"，所以在吃满文心一言的流量的同时，也不可避免地受到了名称的局限。

回顾这个项目的起源，它诞生于我的一个个人项目，当初只是一个模块，后来因为其通用性强，我决定将它独立出来并开源到 GitHub 上。期初其实我并不对其抱有太多的期望，但是在我提交代码的当天晚上，我就收获了两个star，并且在短短几个月中，获得了远超出我预期的maven仓库下载量。谢谢各位对我的信任与支持，你们的支持，是让这个项目坚持走到今天最大的动力。

然而，随着用户增多，问题也接踵而来。因为起初架构设计不够合理与代码质量不够高，导致项目积累了很多问题。几个月后，我不得不尝试进行 2.x 版本的重构，但后期的工作量之大让我难以应对，重构后依旧存在一些技术债，仍然没达到我理想的标准。这也是为什么直到今天，2.0的release版本仍未能正式发布。

但是，即便在几乎停止维护的这几个月里，项目的热度依旧不减。感谢各位的信任，所以我决定不辜负大家的期待，将在不久后继续维护这个项目。不过，未来项目的主体将会变更为社区（名称还未定）。（它不是商业组织，只是为了让项目更加规范化，而主要维护者仍然可能是我）。这样做也是为了回应一些开发者对个人维护项目持续性的担忧。

除此之外，局限在文心系列模型中的确不是一个好的选择，也限制了项目本身能力的发挥。为此，我计划扩展项目的适配范围，并将其重命名为 “llms–nexus”，并对各类大模型提供更完整支持（包括简单易用(甚至无感)的function calling 的完整实现）。当然，文心系列模型作为项目的起点，仍会优先得到支持。

💬 一些心里话

说到改名，我的确怀着一些复杂的心情。新开一个项目可能是更常规的选择，但直接在现有项目基础上大幅修改，或许会让一些早期支持者感到不适。对此，我也深感抱歉。但是对我来说，各位开发者的支持几乎是支撑我继续维护项目的唯一动力。我从未并且也不想走中文社区开源项目的特色"QR Code"。为了让项目能够更长久地走下去，这样的变更是无奈却必要的选择，还请大家理解。

如果你对这次公告有任何疑问，或者不接受这个改变，都欢迎在 issues 区或者通过邮件与我讨论。这份公告只是一个提前的说明，并非最终的决定。


# [ => 1.0版本链接](https://github.com/egmsia01/wenxin-starter/tree/master?tab=readme-ov-file)

## 项目简介
- 百度 **“文心千帆 WENXINWORKSHOP”** 大模型的spring-boot-starter，可以帮助您快速接入百度的AI能力。
- 完整对接文心千帆的官方API文档。
- 支持文生图，内置对话记忆，支持对话的流式返回。
- 支持单个模型的QPS控制，支持排队机制。
- 即将增加插件支持。

## 快速开始

[使用demo (1.x版，2.x请阅读文档) ](https://github.com/rainveil/springboot-wenxin-demo)

*【基于Springboot 3.0开发，所以要求JDK版本为17及以上】*

### 1、添加依赖

- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>wenxin-starter</artifactId>
  <version>2.0.0-beta4</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:wenxin-starter:2.0.0-beta4' 
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
    public ChatModel ernieClient() {
        
        ModelConfig modelConfig = new ModelConfig();
        // 模型名称，需跟设置的QPS数值的名称一致 (建议与官网名称一致)
        modelConfig.setModelName("Ernie");
        // 模型url
        modelConfig.setModelUrl("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions");
        // 单独设置某个模型的access-token, 优先级高于全局access-token, 统一使用全局的话可以不设置
        modelConfig.setAccessToken("xx.xx.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
  
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

@RestController
public class ChatController {

    // 要调用的模型的客户端（示例为文心）
    @Resource
    @Qualifier("Ernie")
    private ChatModel chatClient;
  
    /**
     * chatClient.chatStream(msg) 单轮流式对话
     * chatClient.chatStream(new ChatErnieRequest()) 单轮流式对话, 参数可调
     * chatClient.chatsStream(msg, msgId) 连续对话
     * chatClient.chatsStream(new ChatErnieRequest(), msgId) 连续对话, 参数可调
     */
  
    /**
     * 以下两种方式均可
     */
    // 连续对话，流式
    @GetMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatSingleStream(@RequestParam String msg, @RequestParam String uid) {
        // 单次对话 chatClient.chatStream(msg)
        Flux<ChatResponse> responseFlux = chatClient.chatsStream(msg, uid);
        return responseFlux.map(ChatResponse::getResult);
    }
  
    // 连续对话，流式
    @GetMapping(value = "/stream/chats1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chats(@RequestParam String msg, @RequestParam String uid) {
        SseEmitter emitter = new SseEmitter();
        // 支持参数设置 ChatErnieRequest（Ernie系列模型）、ChatBaseRequest（其他模型）
        // 单次对话 chatClient.chatsStream(msg)
        chatClient.chatsStream(msg, uid).subscribe(response -> {
            try {
                emitter.send(SseEmitter.event().data(response.getResult()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return emitter;
    }

}

    /**
     * Prompt模板被百度改的有点迷，等稳定一下再做适配...
     */

```

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=rainveil/llms–nexus&type=Date)](https://star-history.com/#rainveil/llms–nexus)

## 更新日志

v2.0.0-alpha1  // 始终上传失败...建议自己拉仓库install
- JDK 8专版

v2.0.0 - bata4

- 修复 修复定时任务导致的序列化问题

v2.0.0 - bata3

- 修复 修复并发场景下导致的丢对话任务的问题
- 修复 网络异常情况下导致的消息错乱问题
- 新增 导入导出消息的api
- 新增 消息存储与获取的api
- 新增 Prompt与ImageClient
- 优化 整体性能
- 其余改动请查看commit.

v2.0.0 - bata

！ 2.x 版本与 1.x 版本不兼容
- 重构 SDK架构，大幅提升性能
- 重构 客户端生成方式，支持自定义多模型，不再需要适配
- 完善 普通chat接口现已可用

## 使用文档

<div>
点击跳转 => 
<a href="/doc/wenxin-doc.md">使用文档</a>
</div>

## 开源协议
```text
MIT License

Copyright (c) 2023 Rainveil

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
