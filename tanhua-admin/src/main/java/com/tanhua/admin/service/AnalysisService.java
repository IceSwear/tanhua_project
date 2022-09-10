package com.tanhua.admin.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tanhua.admin.mappers.AnalysisMapper;
import com.tanhua.admin.mappers.LogMapper;
import com.tanhua.model.domain.Analysis;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Service
@Slf4j
public class AnalysisService {

    @Resource
    private AnalysisMapper analysisMapper;
    @Resource
    private LogMapper logMapper;

    public void analysis() throws ParseException {
        String todayStr = new DateTime().toString("yyyy-MM-dd");
        String yesterdayStr = DateUtil.yesterday().toString("yyyy-MM-dd");
        log.info("今天：{},昨天：{}", todayStr, yesterdayStr);
        //注册统计
        Integer regCount = logMapper.queryByTypeAndLogTime("0102", todayStr);
        //登录统计
        Integer loginCount = logMapper.queryByTypeAndLogTime("0101", yesterdayStr);
        //活跃数量
        Integer activeCount = logMapper.queryByLogTime(todayStr);
        //留存数
        Integer remainCount = logMapper.queryNumRetentionId(todayStr, yesterdayStr);
        Date dateOfToday = new SimpleDateFormat("yyyy-MM-dd").parse(todayStr);
        //query by date, null =save ,not null =update
        QueryWrapper<Analysis> qw = new QueryWrapper<>();
        qw.eq("record_date", dateOfToday);
        Analysis analysis = analysisMapper.selectOne(qw);
        if (!Objects.isNull(analysis)) {
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(remainCount);
//            analysis.setUpdated(new Date());
            log.info("更新analysis表:{}", analysis);
            analysisMapper.updateById(analysis);
        } else {
            analysis = new Analysis();
            analysis.setNumRegistered(regCount);
            analysis.setNumLogin(loginCount);
            analysis.setNumActive(activeCount);
            analysis.setNumRetention1d(remainCount);
            analysis.setRecordDate(dateOfToday);
//            analysis.setCreated(new Date());
//            analysis.setUpdated(new Date());
            log.info("analysis表插入新一天数据~:{}", analysis);
            analysisMapper.insert(analysis);
        }
    }

    public Analysis querySummary(Date now) {
        //5、根据当前时间查询AnalysisByDay数据
        LambdaQueryWrapper<Analysis> qw = Wrappers.<Analysis>lambdaQuery();
        qw.eq(Analysis::getRecordDate, DateUtil.format(now, "yyyy-MM-dd"));
        return analysisMapper.selectOne(qw);
    }

    public Integer queryCumulativeUsers() {
        return analysisMapper.queryCumulativeUsers();
    }
}
