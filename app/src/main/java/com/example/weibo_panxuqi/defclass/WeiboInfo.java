package com.example.weibo_panxuqi.defclass;

import java.io.Serializable;
import java.util.List;

/**
 * @auther panxuqi
 * @date 2024/6/14
 * @time 9:28
 */
public class WeiboInfo implements Serializable {

    private long id;
    private long userId;
    private String username;
    private String phone;
    private String avatar;
    private String title;
    private String videoUrl;
    private String poster;
    private List<String> images;
    private int likeCount;
    private boolean likeFlag;
    private String createTime;

    public WeiboInfo(long id, long userId, String username, String phone, String avatar, String title,
                     String videoUrl, String poster, List<String> images, int likeCount, boolean likeFlag, String createTime) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.avatar = avatar;
        this.title = title;
        this.videoUrl = videoUrl;
        this.poster = poster;
        this.images = images;
        this.likeCount = likeCount;
        this.likeFlag = likeFlag;
        this.createTime = createTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(boolean likeFlag) {
        this.likeFlag = likeFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}