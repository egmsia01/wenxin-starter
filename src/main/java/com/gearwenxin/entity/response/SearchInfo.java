package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
class SearchInfo {

    /**
     * 搜索结果的列表
     */
    @JsonProperty("search_results")
    private List<SearchResult> searchResults;

}