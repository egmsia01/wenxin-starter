package com.gearwenxin.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gearwenxin.entity.BaseRequest;

import com.gearwenxin.entity.FunctionInfo;
import com.gearwenxin.entity.Message;
import lombok.*;

import java.util.Deque;
import java.util.List;

/**
 * @author Ge Mingjia
 * {@code @date} 2023/7/20
 * <p>
 * ContBot 模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ErnieRequest extends BaseRequest {

    /**
     * （1）较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定
     * （2）默认0.95，范围 (0, 1.0]，不能为0
     * （3）建议该参数和top_p只设置1个
     * （4）建议top_p和temperature不要同时更改
     */
    @JsonProperty("temperature")
    private Float temperature;

    /**
     * （1）影响输出文本的多样性，取值越大，生成文本的多样性越强
     * （2）默认0.8，取值范围 [0, 1.0]
     * （3）建议该参数和temperature只设置1个
     * （4）建议top_p和temperature不要同时更改
     */
    @JsonProperty("top_p")
    private Float topP;

    /**
     * 通过对已生成的token增加惩罚，减少重复生成的现象。说明：
     * （1）值越大表示惩罚越大
     * （2）默认1.0，取值范围：[1.0, 2.0]
     */
    @JsonProperty("penalty_score")
    private Float penaltyScore;

    /**
     * 一个可触发函数的描述列表
     */
    @JsonProperty("functions")
    private List<FunctionInfo> functions;

    /**
     * 模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手，说明：
     * （1）长度限制1024个字符
     * （2）如果使用functions参数，不支持设定人设system
     */
    @JsonProperty("system")
    private String system;

    /**
     * 生成停止标识，当模型生成结果以stop中某个元素结尾时，停止文本生成。说明：
     * （1）每个元素长度不超过20字符
     * （2）最多4个元素
     */
    @JsonProperty("stop")
    private List<String> stop;

    /**
     * 是否强制关闭实时搜索功能，默认false，表示不关闭
     */
    @JsonProperty("disable_search")
    private Boolean disableSearch;

    /**
     * 是否开启上角标返回，说明：
     * （1）开启后，有概率触发搜索溯源信息search_info，search_info内容见响应参数介绍
     * （2）默认false，不开启
     */
    @JsonProperty("enable_citation")
    private Boolean enableCitation;

    public static ErnieRequestBuilder builder() {
        return new ErnieRequestBuilder();
    }

    public static class ErnieRequestBuilder extends BaseRequestBuilder {
        private Float temperature;
        private Float topP;
        private Float penaltyScore;
        private String userId;
        private Deque<Message> messages;
        private Boolean stream;
        private List<FunctionInfo> functions;
        private String system;
        private List<String> stop;
        private Boolean disableSearch;
        private Boolean enableCitation;

        public ErnieRequestBuilder temperature(Float temperature) {
            this.temperature = temperature;
            return this;
        }

        public ErnieRequestBuilder topP(Float topP) {
            this.topP = topP;
            return this;
        }

        public ErnieRequestBuilder penaltyScore(Float penaltyScore) {
            this.penaltyScore = penaltyScore;
            return this;
        }

        @Override
        public ErnieRequestBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public ErnieRequestBuilder messages(Deque<Message> messages) {
            this.messages = messages;
            return this;
        }

        @Override
        public ErnieRequestBuilder stream(Boolean stream) {
            this.stream = stream;
            return this;
        }

        public ErnieRequestBuilder functions(List<FunctionInfo> functions) {
            this.functions = functions;
            return this;
        }

        public ErnieRequestBuilder system(String system) {
            this.system = system;
            return this;
        }

        public ErnieRequestBuilder stop(List<String> stop) {
            this.stop = stop;
            return this;
        }

        public ErnieRequestBuilder disableSearch(Boolean disableSearch) {
            this.disableSearch = disableSearch;
            return this;
        }

        public ErnieRequestBuilder enableCitation(Boolean enableCitation) {
            this.enableCitation = enableCitation;
            return this;
        }

        @Override
        public ErnieRequest build() {
            ErnieRequest ernieRequest = new ErnieRequest();
            ernieRequest.setTemperature(temperature);
            ernieRequest.setTopP(topP);
            ernieRequest.setPenaltyScore(penaltyScore);
            ernieRequest.setUserId(userId);
            ernieRequest.setMessages(messages);
            ernieRequest.setStream(stream);
            ernieRequest.setFunctions(functions);
            ernieRequest.setSystem(system);
            ernieRequest.setStop(stop);
            ernieRequest.setDisableSearch(disableSearch);
            ernieRequest.setEnableCitation(enableCitation);

            return ernieRequest;
        }
    }

}
