package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tanhua.dubbo.mappers.AnnouncementMapper;
import com.tanhua.model.domain.Announcement;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 公告列表
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
@DubboService
@Slf4j
public class AnnouncementImpl implements AnnouncementApi {
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public PageResult listAccountments(Integer page, Integer pagesize) {

        //here we use mybatis plus
        Page<Announcement> page1 = new Page<>(page, pagesize);
        QueryWrapper qw = new QueryWrapper<>();
        qw.orderByDesc("created");
        Page<Announcement>  page2 = announcementMapper.selectPage(page1, qw);
        long total = page2.getTotal();
        List<Announcement> list1 = page2.getRecords();
        log.info("Mybatis-Plus公告分页查询结果:total总数:{},list:{}", total, list1);


        //hand writing sql + page help
        PageHelper.startPage(page, pagesize);
        List<Announcement> list = announcementMapper.getAllNotDeleted();
        List<Announcement> list2 = new ArrayList<>();
        Long total1 = null;
        if (!CollUtil.isEmpty(list)) {
            PageInfo<Announcement> pageinfo = new PageInfo<>(list);
            total1 = pageinfo.getTotal();
            list2 = pageinfo.getList();
            log.info("hand writing sql + page help分页结果total总数:{},list:{}", total1, list2);
        }
        log.info("hashcode比较{}，{}", list1.hashCode(), list2.hashCode());
        return new PageResult(page, pagesize, total1, list2);
    }
}
