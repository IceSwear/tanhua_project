package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @Description:  关注用户数据层实现类
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@DubboService
@Slf4j
public class FocusUserApiImpl implements FocusUserApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * follow someone 关注用户
     *
     * @param currentUserId
     * @param uid
     */
    @Override
    public void save(Long currentUserId, Long uid) {
        //judge if already exit，确认是否存在
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("followUserId").is(uid));
        boolean exists = mongoTemplate.exists(query, FocusUser.class);
        if (!exists) {
            //不存在，就保存
            FocusUser focusUser = new FocusUser();
            focusUser.setUserId(currentUserId);
            focusUser.setFollowUserId(uid);
            focusUser.setCreated(System.currentTimeMillis());
            log.info("用户未关注FocusUser:{}，需要保存", focusUser);
            mongoTemplate.save(focusUser);
        }
    }


    /**
     * unfollow someone 取关,
     *
     * @param currentUserId
     * @param uid
     */
    @Override
    public void remove(Long currentUserId, Long uid) {
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("followUserId").is(uid));
        FocusUser remove = mongoTemplate.findAndRemove(query, FocusUser.class);
        log.info("取消关注:{}", remove);
    }
}
