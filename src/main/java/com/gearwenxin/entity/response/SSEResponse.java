package com.gearwenxin.entity.response;

import lombok.Data;

@Data
public class SSEResponse {

    private String content;

    @Override
    public String toString() {
        return "data: " + content + "\n\n";
    }

}