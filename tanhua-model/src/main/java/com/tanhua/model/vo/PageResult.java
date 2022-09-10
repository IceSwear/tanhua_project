package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 分页列表信息
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult implements Serializable {

    /**
    总记录数,default is 0L
     */
    private Long counts = 0L;
    /**
    page size ,default is 10;
     */
    private Integer pagesize = 10;
    /**
    total pages,default is 0l
     */
    private Long pages = 0L;

    /**
    current page,default is 1
     */
    private Integer page = 1;
    /**
    list,default is 0
     */
    private List<?> items = new ArrayList<>();


    /**
     * 表属性
     * @param page
     * @param pagesize
     * @param counts
     * @param list
     */
    public PageResult(Integer page, Integer pagesize, Long counts, List list) {
        this.page = page;
        this.pagesize = pagesize;
        this.items = list;
        this.counts = counts;
        //除余,  整除，不能整除+1
        this.pages = counts % pagesize == 0 ? counts / pagesize : counts / pagesize + 1;
    }
}