package com.example.weibo_panxuqi.request;

/**
 * @auther panxuqi
 * @date 2024/6/14
 * @time 10:11
 */
public class WeiboRequest {
    private String token;

    public WeiboRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
