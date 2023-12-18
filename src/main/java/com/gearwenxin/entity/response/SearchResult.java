package com.gearwenxin.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class SearchResult {

    /**
     * 搜索结果的序号
     */
    private int index;

    /**
     * 搜索结果的url
     */
    private String url;

    /**
     * 搜索结果的标题
     */
    private String title;

}