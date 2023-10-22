package com.gearwenxin.entity.response.plugin.knowledge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 * @date 2023/10/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeMIResponses {

    /**
     * 文档分片的下载地址
     */
    @JsonProperty("contentUrl")
    private String contentUrl;

    /**
     * 文档 Id
     */
    @JsonProperty("docId")
    private String docId;

    /**
     * 文档的名称
     */
    @JsonProperty("docName")
    private String docName;

    /**
     * 文档上传的知识库 Id
     */
    @JsonProperty("kbId")
    private String kbId;

    /**
     * 当前分片和用户请求的相关度，取值范围（0-1）
     */
    @JsonProperty("score")
    private float score;

    /**
     * 分片 ID
     */
    @JsonProperty("shardId")
    private String shardId;

    /**
     * 分片序号
     */
    @JsonProperty("shardIndex")
    private Integer shardIndex;

    /**
     * 分片的实际内容
     */
    @JsonProperty("content")
    private String content;

}
