package com.example.weibo_panxuqi.request;

/**
 * @auther panxuqi
 * @date 2024/6/14
 * @time 10:05
 */
public class UserInfoRequest {
    private String token;

    public UserInfoRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
