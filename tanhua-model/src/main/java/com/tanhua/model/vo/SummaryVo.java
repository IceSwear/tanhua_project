package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:  总结Vo(后台的)
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryVo implements Serializable {

    /**
    今年
     */
    private List<?> thisYear = new ArrayList<>();

    /**
   去年
    */
    private List<?> lastYear = new ArrayList<>();
}
