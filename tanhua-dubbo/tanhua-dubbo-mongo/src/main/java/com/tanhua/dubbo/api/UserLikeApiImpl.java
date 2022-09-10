package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;

/**
 * @Description: userLike 实现类层
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@DubboService
@Slf4j
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * save or update the user like status 保存或更新用户喜欢状态
     *
     * @param currentUserId
     * @param likeUserId
     * @param
     * @return
     */
    @Override
    public Boolean saveOrUpdate(Long currentUserId, Long likeUserId, boolean isLike) {
        try {
            //build query condition that userId = current user id,likeUserId =likeUserId,to get the userLike object
            //构造2个id相等条件，得到userlike对象
            Query query = Query.query(Criteria.where("userId").is(currentUserId).and("likeUserId").is(likeUserId));
            UserLike userLike = mongoTemplate.findOne(query, UserLike.class);
            //judge if userLike is null, true=save ,false=update
            //判断userlike是否为空，true则保存，false则更新
            if (Objects.isNull(userLike)) {
                //set userId-LikeUserid-updated-created ,then save
                //设置collection中四个属性的值，然后保存
                userLike = new UserLike();
                userLike.setUserId(currentUserId);
                userLike.setUpdated(System.currentTimeMillis());
                userLike.setCreated(System.currentTimeMillis());
                userLike.setLikeUserId(likeUserId);
                userLike.setIsLike(isLike);
                mongoTemplate.save(userLike);
            } else {
                //set update condition to claire which fields need to be updated
                //构造update对象声明哪些字段需要更新
                Update update = Update.update("isLike", isLike).set("updated", System.currentTimeMillis());
                log.info("userlike---更新isLike字段为{}", isLike);
                mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int loveAloneCount(Long userId) {
        //get counts of loving
        Query query = Query.query(Criteria.where("userId").is(userId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        if (CollUtil.isEmpty(userLikeList)) {
            return 0;
        }
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
        //上面得到的是当前用户喜欢的人，对方也可能也喜欢，所以还要排除,
        //筛选掉 这些用户还没喜欢/或者不喜欢 当前用户
        //反过来，统计这些用户，也喜欢当前用户，然后相减
        Query query1 = Query.query((Criteria.where("userId").in(likeUserIds).and("likeUserId").is(userId).and("isLike").is(true)));
        List<UserLike> userAlsoLikes = mongoTemplate.find(query1, UserLike.class);
        //我喜欢的总数-在这总数也喜欢本人的数量=单方面喜欢的数量
        return userLikeList.size() - userAlsoLikes.size();
    }


    /**
     * be followed count 喜欢我，但我不喜欢就是粉丝
     *
     * @param userId
     * @return
     */
    @Override
    public int fanCount(Long userId) {
        Query query = Query.query(Criteria.where("likeUserId").is(userId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        if (CollUtil.isEmpty(userLikeList)) {
            return 0;
        }
        //上面找的是喜欢我的数量，但是还要当方面喜欢,-在这些id里，我也喜欢的

        //拿到喜欢的 的userId
        List<Long> loveMeUserIds = CollUtil.getFieldValues(userLikeList, "userId", Long.class);

        Query query1 = Query.query(Criteria.where("likeUserId").in(loveMeUserIds).and("userId").is(userId).and("isLike").is(true));
        List<UserLike> iLoveTooList = mongoTemplate.find(query1, UserLike.class);
        //别人喜欢我的数量---在这些人中我也喜欢别人的数量=对方单相思，我的分数

        return userLikeList.size() - iLoveTooList.size();
    }

    @Override
    public int loveEachOtherCount(Long userId) {
        //get counts of i like,我喜欢的集合
        Query query = Query.query(Criteria.where("userId").is(userId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        if (CollUtil.isEmpty(userLikeList)) {
            return 0;
        }
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
        //统计出我喜欢的人，是否也喜欢我的条数
        Query query1 = Query.query((Criteria.where("userId").in(likeUserIds).and("likeUserId").is(userId).and("isLike").is(true)));
        List<UserLike> userAlsoLikes = mongoTemplate.find(query1, UserLike.class);
        //喜欢我的人中---》筛选出我也喜欢的集合
        return userAlsoLikes.size();
    }


    /**
     * 相互喜欢（关注列表）
     *
     * @param currentUserId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult loveEachOtherList(Long currentUserId, Integer page, Integer pagesize) {
        //get counts of i like,我喜欢的集合
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        if (CollUtil.isEmpty(userLikeList)) {
            return new PageResult();
        }
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
        //统计出我喜欢的人，是否也喜欢我的条数
        Query query1 = Query.query((Criteria.where("userId").in(likeUserIds).and("likeUserId").is(currentUserId).and("isLike").is(true)));
        //先统计数量再枫叶
        long count = mongoTemplate.count(query1, UserLike.class);
        //开始分页
        query1.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("updated")));
        List<UserLike> userLikes = mongoTemplate.find(query1, UserLike.class);
        List<Long> userIds = CollUtil.getFieldValues(userLikes, "userId", Long.class);
        log.info("相互喜欢（相互关注的用户ids）:{}", userIds);
        return new PageResult(page, pagesize, count, userIds);
    }


    /**
     * 单相思/只有我喜欢的列表 可能或遇到set？
     *
     * @param currentUserId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult loveAloneList(Long currentUserId, Integer page, Integer pagesize) {

        //get counts of loving
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        if (CollUtil.isEmpty(userLikeList)) {
            return new PageResult();
        }
        List<Long> userIds = CollUtil.getFieldValues(userLikeList, "likeUserId", Long.class);
        //上面得到的是当前用户喜欢的人，对方也可能也喜欢，所以还要排除,
        //筛选掉 这些用户还没喜欢/或者不喜欢 当前用户
        //反过来，统计这些用户，也喜欢当前用户，然后相减
        Query query1 = Query.query((Criteria.where("userId").in(userIds).and("likeUserId").is(currentUserId).and("isLike").is(true)));
        List<UserLike> userAlsoLikes = mongoTemplate.find(query1, UserLike.class);
        List<Long> userAlsoLoveIds = CollUtil.getFieldValues(userAlsoLikes, "userId", Long.class);
        log.info("用户喜欢的ids{},也喜欢用户的ids：{}", userIds, userAlsoLoveIds);
        //x相减 我喜欢的ids
        boolean b = userIds.removeAll(userAlsoLoveIds);
        log.info("removeAll去重操作:{}", b);
        int size = userIds.size();
        log.info("单相思：用户喜欢减去也喜欢用户的的ids---{},大小:{}", userIds, size);
        //进行分页查询
//        List<UserLike> userLikes = mongoTemplate.find(Query.query(Criteria.where("userId").in(userIds).and("likeUserId").is(currentUserId).and("isLike").is(true)).limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("updated"))), UserLike.class);
        //我喜欢的总数-在这总数也喜欢本人的数量=单方面喜欢的数量
        List<Long> pageList = ListUtil.page(page-1, pagesize, userIds);
//        List<Long> userIdsList = CollUtil.getFieldValues(userLikes, "userId", Long.class);
        log.info("分页后单相思的ids:{}", pageList);
        return new PageResult(page, pagesize, Long.valueOf(size), pageList);
    }


    /**
     * be followed count 喜欢我，但我不喜欢就是粉丝 分页查询
     *
     * @param currentUserId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult fanList(Long currentUserId, Integer page, Integer pagesize) {
        //先统计出别人喜欢我的结果
        Query query = Query.query(Criteria.where("likeUserId").is(currentUserId).and("isLike").is(true));
        List<UserLike> userLikeList = mongoTemplate.find(query, UserLike.class);
        if (CollUtil.isEmpty(userLikeList)) {
            return new PageResult();
        }
        //上面找的是喜欢我的数量，但是还要当方面喜欢,-在这些id里，我也喜欢的

        //拿到喜欢我的 的userId,这里可能也有我喜欢的用户
        List<Long> loveMeUserIds = CollUtil.getFieldValues(userLikeList, "userId", Long.class);

        //在这个id方位内我喜欢的用户查出来
        Query query1 = Query.query(Criteria.where("likeUserId").in(loveMeUserIds).and("userId").is(currentUserId).and("isLike").is(true));
        List<UserLike> iLoveTooList = mongoTemplate.find(query1, UserLike.class);
        List<Long> iLoveTooIds = CollUtil.getFieldValues(iLoveTooList, "likeUserId", Long.class);
        //别人喜欢我的数量---在这些人中我也喜欢别人的数量=对方单相思，我的粉丝
        log.info("喜欢我都的ids{},我喜欢的ids{}", loveMeUserIds, iLoveTooIds);
        boolean b = loveMeUserIds.removeAll(iLoveTooIds);
        log.info("removeAll去重操作:{}", b);
        int size = loveMeUserIds.size();
        log.info("单相思--相减后的ids---{},大小:{}", loveMeUserIds, size);
        //进行分页查询
        List<UserLike> userLikes = mongoTemplate.find(Query.query(Criteria.where("userId").in(loveMeUserIds).and("likeUserId").is(currentUserId).and("isLike").is(true)).skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("updated"))), UserLike.class);
        List<Long> userIds = CollUtil.getFieldValues(userLikes, "userId", Long.class);
        log.info("粉丝单相思的ids:{}", userIds);
        return new PageResult(page, pagesize, Long.valueOf(size), userIds);
    }


    /**
     * 取消喜欢
     *
     * @param currentUserId
     * @param userId
     */
    @Override
    public void removeLike(Long currentUserId, Long userId) {
        //userLike表查找，有的话就删了
        UserLike userLike = mongoTemplate.findAndRemove(Query.query(Criteria.where("userId").is(currentUserId).and("likeUserId").is(userId).and("isLike").is(true)), UserLike.class);
        log.info("喜欢列表取消喜欢的地userlike:{}", userLike);
    }

    /**
     * 是否喜欢
     *
     * @param userId
     * @param uid
     * @return
     */
    @Override
    public Boolean alreadyLove(Long userId, String uid) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("likeUserId").is(uid).and("isLike").is(true));
        return mongoTemplate.exists(query, UserLike.class);
    }


}
