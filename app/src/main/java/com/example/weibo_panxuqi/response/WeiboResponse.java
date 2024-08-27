package com.example.weibo_panxuqi.response;

import com.example.weibo_panxuqi.defclass.Page;
import com.example.weibo_panxuqi.defclass.WeiboInfo;

/**
 * @auther panxuqi
 * @date 2024/6/14
 * @time 10:11
 */
public class WeiboResponse {
    private int code;
    private String meg;
    private Page<WeiboInfo> data;

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

    public Page<WeiboInfo> getData() {
        return data;
    }

    public void setData(Page<WeiboInfo> data) {
        this.data = data;
    }
}