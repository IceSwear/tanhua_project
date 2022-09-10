package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Description: 分析总结Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisSummaryVo {
    /**
     * 累计用户数
     */
    private Long cumulativeUsers=233L;
    /**
     * 过去30天活跃用户数
     */
    private Long activePassMonth=3322L;
    /**
     * 过去7天活跃用户
     */
    private Long activePassWeek=2424L;
    /**
     * 今日新增用户数量
     */
    private Long newUsersToday=232L;
    /**
     * 今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private BigDecimal newUsersTodayRate=new BigDecimal("2.33");
    /**
     * 今日登录次数
     */
    private Long loginTimesToday=242L;
    /**
     * 今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
     */
    private BigDecimal loginTimesTodayRate=new BigDecimal("23.433");
    /**
     * 今日活跃用户数量
     */
    private Long activeUsersToday=2421L;
    /**
     * 今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
     */
    private BigDecimal activeUsersTodayRate=new BigDecimal("2444.33");


}