package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 访客实现类
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
@DubboService
@Slf4j
public class VisitorsApiImpl implements VisitorsApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存访客记录（对于同一个用户，只保存一次访客数据）
     *
     * @param visitors
     */
    @Override
    public void saveVisitors(Visitors visitors) {
        //查询当前访客是否和被放着是否存在在当天，如果不存在就保存
        Query query = Query.query(Criteria.where("userId").is(visitors.getUserId()).and("visitorUserId").is(visitors.getVisitorUserId()).and("visitDate").is(visitors.getVisitDate()));
        if (!mongoTemplate.exists(query, Visitors.class)) {
            log.info("记录不存在，保存访客记录{}", visitors);
            mongoTemplate.save(visitors);
        } else {
            log.info("访客记录已存在，不进行保存");
        }
    }


    /**
     * 根据id和查询时间得到访客列表
     *
     * @param queryTime
     * @param userId
     * @return
     */
    @Override
    public List<Visitors> getVisitorsListByIdAndQueryTime(Long queryTime, Long userId) {
        //被访者是我，访客不是我
        Criteria criteria = Criteria.where("userId").is(userId).and("visitorUserId").ne(userId);
        if (!Objects.isNull(queryTime)) {
            criteria.and("date").gt(queryTime);
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("date"))).limit(5);
        List<Visitors> visitors = mongoTemplate.find(query, Visitors.class);
        log.info("mongo最近访客：{}", visitors);
        return visitors;
    }


    /**
     * visitors list 访客记录
     *
     * @param currentUserId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult visitorsList(Long currentUserId, Integer page, Integer pagesize) {
        //条件，被访者是我，但是访客不是我
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("visitorUserId").ne(currentUserId));
        long count = mongoTemplate.count(query, Visitors.class);
        //再分页
        query.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("date")));
        List<Visitors> visitors = mongoTemplate.find(query, Visitors.class);
        List<Long> visitorUserIds = CollUtil.getFieldValues(visitors, "visitorUserId", Long.class);
        log.info("符合条件的访客ids:{}", visitorUserIds);
        return new PageResult(page, pagesize, count, visitorUserIds);
    }
}
