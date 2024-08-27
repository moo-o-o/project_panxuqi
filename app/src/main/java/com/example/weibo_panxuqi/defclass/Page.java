package com.example.weibo_panxuqi.defclass;

import java.util.List;

/**
 * @auther panxuqi
 * @date 2024/6/14
 * @time 10:06
 */
public class Page<T> {
    private List<T> records;
    private int total;
    private int size;
    private int current;
    private int pages;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
