package com.cheese.shiro.common.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 权限服务内部分页信息包装类
 *
 * @author sobann
 */
public class SPage<T> implements Serializable {
    private static final long serialVersionUID = 8971689564757259832L;
    private int current;
    private int total;
    private int pages;
    private int size;
    private List<T> records;

    public SPage(int current, int size) {
        this.current = current;
        this.size = size;
    }

    public SPage(int current, int size, int total, List<T> records) {
        this.current = current;
        this.total = total;
        this.pages = (total + size - 1) / size;
        this.size = size;
        this.records = records;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        this.pages = (total + size - 1) / size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
