package com.example.weibo_panxuqi.request;

/**
 * @auther panxuqi
 * @date 2024/6/16
 * @time 9:18
 */
public class UnlikeRequest {
    private Long id;

    public UnlikeRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}