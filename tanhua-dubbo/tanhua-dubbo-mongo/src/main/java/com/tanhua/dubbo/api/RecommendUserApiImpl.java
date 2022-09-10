package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@DubboService
@Slf4j
public class RecommendUserApiImpl<T> implements RecommendUserApi<T> {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询最高分
     *
     * @param toUserId
     * @return
     */
    public RecommendUser queryWithMaxScore(Long toUserId) {
        //根据toUserId查询，根据评分score排序，获取第一条
        //构建Criteria
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //构建Query对象
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        if (Objects.isNull(recommendUser)) {
            Query query1 = new Query(Criteria.where("userId").ne(toUserId)).with(Sort.by(Sort.Order.desc("score"))).limit(1);
            recommendUser = mongoTemplate.findOne(query1, RecommendUser.class);
            log.info("没有具体该用户的推荐用户，所以在数据库中找一个最高分的:{}", recommendUser);
        }
        return recommendUser;
    }

    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        //build query condition,构造查询条件对象
        Query query = Query.query(Criteria.where("toUserId").is(toUserId));
        //query count,查询总数
        long count = mongoTemplate.count(query, RecommendUser.class);
        //查询数据列表
//        query.with(Sort.by(Sort.Order.desc("score"))).limit((page - 1) * pagesize).skip(pagesize);
        query.with(Sort.by(Sort.Order.desc("score"))).limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("date")));
        List<RecommendUser> list = mongoTemplate.find(query, RecommendUser.class);
        log.info("MONGO-LIST:{}", list);
        //return PageResult
        return new PageResult(page, pagesize, count, list);
    }


    /**
     * 查看佳人个人信息
     *
     * @param userId
     * @param toUserId
     * @return
     */
    @Override
    public RecommendUser queryByUserId(Long userId, Long toUserId) {
        Query query = new Query(Criteria.where("toUserId").is(toUserId).and("userId").is(userId));
        RecommendUser user = mongoTemplate.findOne(query, RecommendUser.class);
        //judge if one is null
        //判断如果recommneruser为空
        if (Objects.isNull(user)) {
            Query query1 = new Query(Criteria.where("userId").ne(toUserId)).with(Sort.by(Sort.Order.desc("date"))).limit(1);
            user = mongoTemplate.findOne(query1, RecommendUser.class);
            log.info("没有具体该用户的推荐用户，所以在数据库中找一个最新的:{}", user);
        }
        return user;
    }

    /**
     * 查询cardlist
     *
     * @param currentUserId
     * @param counts
     * @return
     */
    @Override
    public List<RecommendUser> queryCardsList(Long currentUserId, int counts) {
        //query like or dislike ids from collection-user_like
        //从集合user_like中查找当前用户对于历史喜欢和不喜欢
        List<UserLike> likeList = mongoTemplate.find(Query.query(Criteria.where("userId").is(currentUserId)), UserLike.class);
        //use hu-tool to get list of like userId
        //用hutool得到喜厌历史的用户id
        List<Long> likeUserId = CollUtil.getFieldValues(likeList, "likeUserId", Long.class);
        //set condition that 1.toUserId is current user id 2. they are not in list of likeUserId
        //构造条件：1.推荐给的用户id是当前用户，2他们不在likeuserId的list中
        //喜欢或不喜欢的筛选出来，不再在卡片里显示
        //下面就是抽烟
        Criteria criteria = Criteria.where("toUserId").is(currentUserId).and("userId").nin(likeUserId);
        //
        TypedAggregation<RecommendUser> recommendUserTypedAggregation = TypedAggregation.newAggregation(RecommendUser.class, Aggregation.match(criteria), Aggregation.sample(counts));
        AggregationResults<RecommendUser> results = mongoTemplate.aggregate(recommendUserTypedAggregation, RecommendUser.class);
        return results.getMappedResults();
    }

//    @Override
//    public void addBath(List<RecommendUser> list) {
//        mongoTemplate.insert(list, RecommendUser.class);
//    }

    @Override
    public void addBath(Collection<? extends T> batchToSave, Class<?> entityClass) {
        mongoTemplate.insert(batchToSave, entityClass);
    }


    /**
     * 删除所有remove all
     */
    @Override
    public void removeAll() {
        Query query = new Query();
        mongoTemplate.remove(query, RecommendUser.class);
    }

//    @Override
//    public void addBath(Set<RecommendUser> set) {
//        mongoTemplate.insert(set, RecommendUser.class);
//    }
}