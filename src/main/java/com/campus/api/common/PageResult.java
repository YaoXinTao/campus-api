package com.campus.api.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, List<T> list) {
        this.pageNum = 1;
        this.pageSize = list.size();
        this.total = total;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    public static <T> PageResult<T> empty(Integer pageNum, Integer pageSize) {
        return new PageResult<>(pageNum, pageSize, 0L, List.of());
    }
}