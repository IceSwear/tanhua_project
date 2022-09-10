package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RemainingTimes;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @Description: 剩余次数持久层实现类
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@DubboService
public class RemainingTimesApiImpl implements RemainingTimesApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 查找剩余次数by user id
     *
     * @param currentUserId
     * @return
     */
    @Override
    public RemainingTimes findByUserId(Long currentUserId) {
        Query query = Query.query(Criteria.where("userId").is(currentUserId));
        boolean exists = mongoTemplate.exists(query, RemainingTimes.class);
        if (!exists) {
            RemainingTimes rt = new RemainingTimes();
            rt.setUserId(currentUserId);
            rt.setRemainingTimes(10);
            RemainingTimes save = mongoTemplate.save(rt);
            log.info("新增：rt{}", save);
        }
        Update update = new Update();
        update.inc("remainingTimes", -1);
        log.info("存在，并次数-1");
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        //修改并返回
        RemainingTimes rt = mongoTemplate.findAndModify(query, update, options, RemainingTimes.class);
        return rt;
    }
}
