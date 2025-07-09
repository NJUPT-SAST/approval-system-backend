package com.sast.approval.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageParamDTO implements Serializable {
    /**
     * 总页数
     */
    private long pageCount;
    /**
     * 当前页数
     */
    private long pageNum;
    /**
     * 每页显示数
     */
    private long pageSize;
}
