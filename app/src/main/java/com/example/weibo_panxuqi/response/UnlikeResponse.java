package com.example.weibo_panxuqi.response;

/**
 * @auther panxuqi
 * @date 2024/6/16
 * @time 9:19
 */
public class UnlikeResponse {
    private int code;
    private String msg;
    private boolean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}