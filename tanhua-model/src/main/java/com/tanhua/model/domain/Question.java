package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Description: 陌生人问题 questions of stranger
 * @Author: Spike Wong
 * @Date: 2022/8/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BasePojo {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 内容
     */
    private String txt;

    /**
     * 逻辑删除 Y N
     */
    private String isDeleted;
}