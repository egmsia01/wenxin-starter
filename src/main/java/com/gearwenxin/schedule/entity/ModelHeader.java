package com.gearwenxin.schedule.entity;

import lombok.Getter;
import lombok.Setter;

public class ModelHeader {

    private Integer X_Ratelimit_Limit_Requests;
    private Integer X_Ratelimit_Limit_Tokens;
    private Integer X_Ratelimit_Remaining_Requests;
    private Integer X_Ratelimit_Remaining_Tokens;

    @Getter
    @Setter
    private String authorization;

    public Integer get_X_Ratelimit_Limit_Requests() {
        return X_Ratelimit_Limit_Requests;
    }

    public void set_X_Ratelimit_Limit_Requests(Integer x_Ratelimit_Limit_Requests) {
        X_Ratelimit_Limit_Requests = x_Ratelimit_Limit_Requests;
    }

    public Integer get_X_Ratelimit_Limit_Tokens() {
        return X_Ratelimit_Limit_Tokens;
    }

    public void set_X_Ratelimit_Limit_Tokens(Integer x_Ratelimit_Limit_Tokens) {
        X_Ratelimit_Limit_Tokens = x_Ratelimit_Limit_Tokens;
    }

    public Integer get_X_Ratelimit_Remaining_Requests() {
        return X_Ratelimit_Remaining_Requests;
    }

    public void set_X_Ratelimit_Remaining_Requests(Integer x_Ratelimit_Remaining_Requests) {
        X_Ratelimit_Remaining_Requests = x_Ratelimit_Remaining_Requests;
    }

    public Integer get_X_Ratelimit_Remaining_Tokens() {
        return X_Ratelimit_Remaining_Tokens;
    }

    public void set_X_Ratelimit_Remaining_Tokens(Integer x_Ratelimit_Remaining_Tokens) {
        X_Ratelimit_Remaining_Tokens = x_Ratelimit_Remaining_Tokens;
    }

}
