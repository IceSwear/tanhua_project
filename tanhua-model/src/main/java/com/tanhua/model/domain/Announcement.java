package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 公告 announcement
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement extends BasePojo {
    /**
     * id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 逻辑删除***
     */
    private String isDeleted;
}
