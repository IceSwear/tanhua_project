package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.Friend;
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
 * @Description: friendapi 实现类
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@DubboService
@Slf4j
public class FriendApiImpl implements FriendApi {

    @Autowired
    MongoTemplate mongoTemplate;


    /**
     * love each other save in mongo
     *
     * @param currentUserId
     * @param friendId
     */
    @Override
    public void saveRelationship(Long currentUserId, Long friendId) {
        //save current user's friend relationship
        //保存当前用户的好友关系
        Query query = new Query(Criteria.where("userId").is(currentUserId).and("friendId").is(friendId));
        boolean exists = mongoTemplate.exists(query, Friend.class);
        if (!exists) {
            Friend friend = new Friend();
            friend.setUserId(currentUserId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
        //save friends to current user's friend relationship
        //保存用户对于当前用户的好友关系
        Query query1 = new Query(Criteria.where("userId").is(friendId).and("friendId").is(currentUserId));
        boolean exists1 = mongoTemplate.exists(query1, Friend.class);
        if (!exists1) {
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(currentUserId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
    }


    /**
     * 得到PageReust
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findPageReusltById(Long userId, Integer page, Integer pagesize) {
        Query query = new Query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Order.desc("created")));
        long count = mongoTemplate.count(query, Friend.class);
        query.limit(pagesize).skip((page - 1) * pagesize);
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        log.info("friends列表:{}", friends);
        return new PageResult(page, pagesize, count, friends);
    }

    /**
     * remove friends relationship in Mongo 删除好友关系
     *
     * @param currentUserId
     * @param friendId
     */
    @Override
    public void remove(Long currentUserId, Long friendId) {
        //query,if friend is not null,remove
        //查询，如果存在，则删除
        Query query = new Query(Criteria.where("userId").is(currentUserId).and("friendId").is(friendId));
        Friend friend = mongoTemplate.findOne(query, Friend.class);
        if (!Objects.isNull(friend)) {
            log.info("存在，删除");
            mongoTemplate.remove(friend);
        }
        //query,if friend is not null,remove
        //查询，如果存在，则删除
        Query query1 = new Query(Criteria.where("userId").is(friendId).and("friendId").is(currentUserId));
        Friend friend1 = mongoTemplate.findOne(query1, Friend.class);
        if (!Objects.isNull(friend1)) {
            log.info("存在，删除");
            mongoTemplate.remove(friend1);
        }
    }

    /**
     * 相互喜欢即是朋友 love each other =friends
     *
     * @param userId
     * @return
     */
    @Override
    public int countFriendsById(Long userId) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<Friend> friends = mongoTemplate.find(query, Friend.class);
        if (CollUtil.isEmpty(friends)) {
            return 0;
        }
        return friends.size();
    }


    /**
     * 获取好友id
     *
     * @param userId
     * @return
     */
    @Override
    public List<Long> getFriendsIds(Long userId) {
        //第一次获得当前好友的friendslist
        Query query1 = Query.query(Criteria.where("userId").is(userId));
        List<Friend> friends = mongoTemplate.find(query1, Friend.class);
        List<Long> friendIds = CollUtil.getFieldValues(friends, "friendId", Long.class);
        //双重认证
        List<Friend> doubleCheckList = mongoTemplate.find(Query.query(Criteria.where("userId").in(friendIds).and("friendId").is(userId)), Friend.class);
        log.info("mongo现有好友：{}", doubleCheckList);
        return CollUtil.getFieldValues(doubleCheckList, "userId", Long.class);
    }
}
