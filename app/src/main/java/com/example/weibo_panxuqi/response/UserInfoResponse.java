package com.example.weibo_panxuqi.response;

import com.example.weibo_panxuqi.defclass.UserInfo;

/**
 * @auther panxuqi
 * @date 2024/6/14
 * @time 10:06
 */
public class UserInfoResponse {
    private int code;
    private String meg;
    private UserInfo data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMeg() {
        return meg;
    }

    public void setMeg(String meg) {
        this.meg = meg;
    }

    public UserInfo getData() {
        return data;
    }

    public void setData(UserInfo data) {
        this.data = data;
    }
}
