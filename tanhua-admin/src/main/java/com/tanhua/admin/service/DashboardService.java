package com.tanhua.admin.service;

import com.tanhua.admin.mappers.AnalysisMapper;
import com.tanhua.admin.mappers.LogMapper;
import com.tanhua.model.vo.CountVo;
import com.tanhua.model.vo.SummaryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */

@Slf4j
@Service
public class DashboardService {

    @Resource
    private AnalysisMapper analysisMapper;
    @Resource
    private LogMapper logMapper;

    public SummaryVo count() {
        log.info("");
        List<CountVo> thisYear = new ArrayList<>();
        List<CountVo> lastYear = new ArrayList<>();
        thisYear.add(new CountVo("1",6959));
        thisYear.add(new CountVo("2",7219));
        thisYear.add(new CountVo("3",9391));
        thisYear.add(new CountVo("4",8141));
        thisYear.add(new CountVo("5",7891));
        thisYear.add(new CountVo("6",4651));
        thisYear.add(new CountVo("7",2447));
        thisYear.add(new CountVo("8",2784));
        thisYear.add(new CountVo("9",6222));
        thisYear.add(new CountVo("10",707));
        thisYear.add(new CountVo("11",7741));
        thisYear.add(new CountVo("12",7741));
        //last year
        lastYear.add(new CountVo("1",1239));
        lastYear.add(new CountVo("2",639));
        lastYear.add(new CountVo("3",4346));
        lastYear.add(new CountVo("4",8087));
        lastYear.add(new CountVo("5",8417));
        lastYear.add(new CountVo("6",2539));
        lastYear.add(new CountVo("7",7735));
        lastYear.add(new CountVo("8",5672));
        lastYear.add(new CountVo("9",7801));
        lastYear.add(new CountVo("10",108));
        lastYear.add(new CountVo("11",2842));
        lastYear.add(new CountVo("12",7039));
        SummaryVo vo=new SummaryVo(thisYear,lastYear);
        return vo;
    }
}
