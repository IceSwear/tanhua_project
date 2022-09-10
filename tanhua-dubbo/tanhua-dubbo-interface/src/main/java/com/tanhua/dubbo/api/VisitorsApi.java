package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @Description:  访客API
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
public interface VisitorsApi {

    /**
     * 保存访客记录
     * @param visitors
     */
    void saveVisitors(Visitors visitors);


    /**
     * 根据id和查询时间得到访客列表
     * @param queryTime
     * @param userId
     * @return
     */
    List<Visitors> getVisitorsListByIdAndQueryTime(Long queryTime,Long userId);

    /**
     * visitors list 访客记录
     * @param currentUserId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult visitorsList(Long currentUserId, Integer page, Integer pagesize);
}
