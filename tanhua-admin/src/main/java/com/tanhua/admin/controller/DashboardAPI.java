package com.tanhua.admin.controller;

import cn.hutool.core.date.DateUtil;
import com.tanhua.admin.service.AnalysisService;
import com.tanhua.admin.service.DashboardService;
import com.tanhua.model.domain.Analysis;
import com.tanhua.model.vo.AnalysisSummaryVo;
import com.tanhua.model.vo.SummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * @Description:  后台控制面板
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardAPI {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/users")
    public ResponseEntity users(Long sd, Long ed, Integer type) {
        log.info("{}---{}----{}", sd, ed, type);
        SummaryVo vo = dashboardService.count();
        return ResponseEntity.ok(vo);
    }

    /**
     * 控制面板统计
     * @return
     */
    @GetMapping("/summary")
    public ResponseEntity summary() {
        log.info("统计summary");
        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();

        Date now = Calendar.getInstance().getTime();

        //累计用户数
        Integer total = analysisService.queryCumulativeUsers();
        analysisSummaryVo.setCumulativeUsers(Long.valueOf(total));

        //查询今日统计信息
        Analysis today_analysis = analysisService.querySummary(now);

        //过去30天活跃用户
        analysisSummaryVo.setActivePassMonth(Long.valueOf(today_analysis.getNumActive30()));

        //过去7天活跃用户
        analysisSummaryVo.setActivePassWeek(Long.valueOf(today_analysis.getNumActive7()));

        //今日活跃用户
        analysisSummaryVo.setActiveUsersToday(Long.valueOf(today_analysis.getNumActive()));

        //今日新增用户
        analysisSummaryVo.setNewUsersToday(Long.valueOf(today_analysis.getNumRegistered()));

        //今日新增用户涨跌率，单位百分数，正数为涨，负数为跌
        //查询昨日统计信息
        Analysis yes_analysis = analysisService.querySummary(DateUtils.addDays(now, -1));
        if (Objects.isNull(yes_analysis)) {
            return ResponseEntity.ok(new AnalysisSummaryVo());
        }
        analysisSummaryVo.setNewUsersTodayRate(computeRate(Long.valueOf(today_analysis.getNumRegistered()), Long.valueOf(yes_analysis.getNumRegistered())));

        //今日登录次数
        analysisSummaryVo.setLoginTimesToday(Long.valueOf(today_analysis.getNumLogin()));

        //今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setLoginTimesTodayRate(computeRate(Long.valueOf(today_analysis.getNumLogin()), Long.valueOf(yes_analysis.getNumLogin())));

        //活跃用户涨跌率，单位百分数，正数为涨，负数为跌
        analysisSummaryVo.setActiveUsersTodayRate(computeRate(Long.valueOf(today_analysis.getNumActive()), Long.valueOf(yes_analysis.getNumActive())));

        return ResponseEntity.ok(analysisSummaryVo);

    }


    /**
     * 计算率
     * @param current
     * @param last
     * @return
     */
    private static BigDecimal computeRate(Long current, Long last) {
        BigDecimal result;
        if (last == 0) {
            // 当上一期计数为零时，此时环比增长为倍数增长
            result = new BigDecimal((current - last) * 100);
        } else {
            result = BigDecimal.valueOf((current - last) * 100).divide(BigDecimal.valueOf(last), 2, BigDecimal.ROUND_HALF_DOWN);
        }
        return result;
    }

    private static String offsetDay(Date date, int offSet) {
        return DateUtil.offsetDay(date, offSet).toDateStr();
    }

}
