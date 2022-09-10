package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:  统计粉丝数等Vo
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountsVo implements Serializable {

    /**
     * 互相喜欢
     */
    Integer eachLoveCount;
    /**
     * 我喜欢
     */
    Integer loveCount;
    /**
     * 粉丝（他人喜欢）
     */
    Integer fanCount;
}
