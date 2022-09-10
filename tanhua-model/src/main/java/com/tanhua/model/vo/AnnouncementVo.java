package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:  公告Vo
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementVo implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private String createDate;

}
