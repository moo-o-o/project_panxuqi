package com.example.weibo_panxuqi.request;

/**
 * @auther panxuqi
 * @date 2024/6/16
 * @time 9:15
 */
public class LikeRequest {
    private Long id;

    public LikeRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
