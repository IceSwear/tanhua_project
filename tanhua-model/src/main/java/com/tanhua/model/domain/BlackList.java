package com.tanhua.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Description: 黑名单 black list
 * @Author: Spike Wong
 * @Date: 2022/8/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackList extends BasePojo {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 被拉黑id
     */
    private Long blackUserId;

    /**
     * 逻辑删除 Y N
     */
    private String isDeleted;
}