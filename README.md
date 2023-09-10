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

## 在线体验：[文心千帆](http://mopen.cloud/)  (被打了，暂时关闭)

## 获取access-token方法
![image](https://github.com/gemingjia/gear-wenxinworkshop-starter/assets/80268501/7225fb98-761a-4ead-b626-d59fc0931161)
可直接查看下面文章：

[申请文心一言&文心千帆大模型API资格、获取access_token，并使用SpringBoot接入文心一言API](https://juejin.cn/post/7260418945721991227)

---

1. 前往 [文心一言资格申请](https://cloud.baidu.com/product/wenxinworkshop) 

2. [填写问卷](https://cloud.baidu.com/survey/qianfan.html)，等待审核通过（我用了一天半）

3. 审核通过后进入 [控制台](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/overview/index)，点击[创建应用](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/create)
4. 进入左侧 [应用列表](https://console.bce.baidu.com/ai/?_=#/ai/wenxinworkshop/app/list)，复制 `API Key` 与 `Secret Key`
5. 将你的 `API Key` 与 `Secret Key` 替换链接的[Key]，访问以下地址
 > https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=[API-Key]&client_secret=[Secret-Key]

## 项目简介
- 百度 **“文心千帆 WENXINWORKSHOP”** 大模型的spring-boot-starter，可以帮助您快速接入百度的AI能力，只需一行代码即可调用百度文心千帆大模型。
- 完整对接文心千帆的官方API文档。
- 支持根据提示词生成图片。
- 支持对话的流式返回。
- 完整支持文心千帆官网所有模型API，包括  `文心一言 ErnieBot`、`ERNIE-Bot-turbo`、`BLOOMZ-7B`、`Ernie-Bot-VilG`、`VisualGLM-6B`、`Llama-2`、`Linly-Chinese-LLaMA-2-7B`、`Linly-Chinese-LLaMA-2-13B`、`ChatGLM2-6B`、`RWKV-4-World`、`OpenLLaMA-7B`、`Falcon-7B`、`Dolly-12B`、`MPT-7B-Instruct`、`Stable-Diffusion-v1.5`、`RWKV-4-pile-14B`、`RWKV-5-World`、`RWKV-Raven-14B`、`Falcon-40B`、`MPT-30B-instruct`、`Flan-UL2`、`Cerebras-GPT-13B`、`Cerebras-GPT-6.7B`、`Pythia-12B`、`Pythia-6.9B`、`GPT-J-6B`、`GPT-NeoX-20B`、`OA-Pythia-12B-SFT-4`、`GPT4All-J`、`StableLM-Alpha-7B` 、 `StarCoder`、`Prompt模板`模型的API（单轮对话、连续对话、流式返回、图片生成）。
- 后续将增加更多模型的支持。

## 快速开始

[使用demo](https://github.com/gemingjia/springboot-wenxin-demo)

```text
除"ErnieBot"与"Prompt"外，其余的对话型模型接收参数类统一为 ChatBaseRequest，响应类为 ChatResponse
图片生成型模型接收参数类统一为 ChatImageRequest，响应类为 ImageResponse，内容为base64编码的图片。
```

### 1、添加依赖

Repository Path: [/io/github/gemingjia/gear-wenxinworkshop-starter)

- Maven
```xml
<dependency>
  <groupId>io.github.gemingjia</groupId>
  <artifactId>gear-wenxinworkshop-starter</artifactId>
  <version>0.0.8</version>
</dependency>
```
- Gradle
```gradle
dependencies {
  implementation 'io.github.gemingjia:gear-wenxinworkshop-starter:0.0.8' 
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

  // 单次对话（阻塞）
  @PostMapping("/chat")
  public BaseRsponse<ChatResponse> chatSingle(String msg) {
    return ernieBotClient.chatSingle(msg).block();
  }

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

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=gemingjia/gear-wenxinworkshop-starter&type=Date)](https://star-history.com/#gemingjia/gear-wenxinworkshop-starter)

## 更新日志
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
[LICENSE](https://www.apache.org/licenses/LICENSE-2.0)
