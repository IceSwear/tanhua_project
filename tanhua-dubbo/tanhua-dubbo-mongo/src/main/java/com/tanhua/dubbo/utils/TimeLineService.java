package com.tanhua.dubbo.utils;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.MovementTimeLine;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:  时间线表服务
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Component
public class TimeLineService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Async
    //底层使用多线程实现
    public void saveTimeLine(Long userId, ObjectId movementId) {
        //1、查询当前用户的好友数据，查找好友表
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria);
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        //2、循环好友数据，构建时间线数据存入数据库
        for (Friend friend : friends) {
            MovementTimeLine timeLine = new MovementTimeLine();
            timeLine.setMovementId(movementId);
            timeLine.setUserId(friend.getUserId());
            timeLine.setFriendId(friend.getFriendId());
            timeLine.setCreated(System.currentTimeMillis());
            mongoTemplate.save(timeLine);
        }
    }
}