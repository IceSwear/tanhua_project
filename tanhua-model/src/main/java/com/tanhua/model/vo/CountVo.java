package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:  后台统计VO
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountVo implements Serializable {

    /*
    日期
     */
    private String title;

    /*
    数量
     */
    private Integer amount;
}
