package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 动态数据库层实现类
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@DubboService
@Slf4j
public class MovementApiImpl implements MovementApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    IdWorker idWorker;

    @Autowired
    TimeLineService timeLineService;

    /**
     * save movement and return movementId
     *
     * @param movement
     * @return
     */
    @Override
    public String saveMovement(Movement movement) {
        //movement- collection
        movement.setPid(idWorker.getNextId("movement"));
        movement.setCreated(System.currentTimeMillis());
        mongoTemplate.save(movement);
        //query friends relationship
//        Criteria criteria = Criteria.where("userId").is(movement.getUserId());
//        Query query = new Query(criteria);
//        List<Friend> friends = mongoTemplate.find(query, Friend.class);
//        //循环好友数据，构建时间线数据存入数据库
//        for (Friend friend : friends) {
//            //new an object of time line
//            MovementTimeLine movementTimeLine = new MovementTimeLine();
//            //save movemend id ,userid,friendid and created
//            movementTimeLine.setMovementId(movement.getId());
//            movementTimeLine.setUserId(movement.getUserId());
//            movementTimeLine.setFriendId(friend.getFriendId());
//            movementTimeLine.setCreated(System.currentTimeMillis());
//            mongoTemplate.save(movementTimeLine);
//        }
        //将调用api查询遍历的事 开启异步处理
        timeLineService.saveTimeLine(movement.getUserId(), movement.getId());
        //返回动态id
        return movement.getId().toHexString();
    }

    @Override
    public PageResult findByUserId(Long userId, Integer state, Integer page, Integer pagesize) {
        Query query = new Query();
        if (!Objects.isNull(userId)) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        if (!Objects.isNull(state)) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        long count = mongoTemplate.count(query, Movement.class);
        query.with(Sort.by(Sort.Order.desc("created"))).limit(pagesize).skip((page - 1) * pagesize);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        //count 也可以为0
        return new PageResult(page, pagesize, count, movements);
    }


    /**
     * query by pid,to get movement,根据id得到movement
     *
     * @param id
     * @return
     */
    @Override
    public Movement findById(String id) {
        Movement one = mongoTemplate.findById(id, Movement.class);
        return one;
    }


    /**
     * query current user's friends' mvm 查询当前用户好友的动态
     *
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    @Override
    public PageResult findFriedMovementsList(Integer page, Integer pagesize, Long userId, List ids) {
        //query timeline collections to get list of mvm_timeline
        //根据friedId查询得到 时间线表的listm，此时我就是朋友的身份
        Query query = new Query(Criteria.where("friendId").is(userId).and("userId").in(ids));
        List<MovementTimeLine> movementTimeLines = mongoTemplate.find(query, MovementTimeLine.class);
        log.info("动态&&好友时间线时间线表:{}", movementTimeLines);
        // set a list of Object id to query and get the movments's list
        //从上方时间线表去构造ObjectId的list，得到movements的list
        //查询出时间线表（对**可见，得到可见的动态id）
        List<ObjectId> movementIds = CollUtil.getFieldValues(movementTimeLines, "movementId", ObjectId.class);
        Query movementQuery = Query.query(Criteria.where("id").in(movementIds).and("state").is(1));
        //先统计再分页
        long count = mongoTemplate.count(movementQuery, Movement.class);
        //分页
        movementQuery.limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movements = mongoTemplate.find(movementQuery, Movement.class);
        log.info("好友分页动态:{}", movements);

        return new PageResult(page, pagesize, count, movements);
    }


    /**
     * random to get mvms, 随机查询
     *
     * @param counts
     * @return
     */
    @Override
    public List<Movement> randomMovements(Integer counts, Long currentUserId) {
        //创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(counts), Aggregation.match(Criteria.where("state").is(1).and("userId").ne(currentUserId)));
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        return results.getMappedResults();
    }

    /**
     * get mvms by pids,根据pids查询mvms
     *
     * @param pids
     * @return
     */
    @Override
    public List<Movement> findMovementsByPids(List<Long> pids) {
        Query query = new Query(Criteria.where("pid").in(pids).and("state").is(1));
        return mongoTemplate.find(query, Movement.class);
    }


    /**
     * query by publishedUserId
     *
     * @param userId
     * @param commentType
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findByPublishUserIdAndCommentType(Long userId, CommentType commentType, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("publishUserId").is(userId);
        if (commentType.equals(CommentType.LIKE)) {
            criteria.and("commentType").is(CommentType.LIKE.getType());
        }
        if (commentType.equals(CommentType.LOVE)) {
            criteria.and("commentType").is(CommentType.LOVE.getType());
        }
        if (commentType.equals(CommentType.COMMENT)) {
            criteria.and("commentType").is(CommentType.COMMENT.getType());
        }
        Query query = Query.query(criteria).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        long count = mongoTemplate.count(query, Comment.class);
        query.skip((page - 1) * pagesize);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        log.info("coments列表{},总数count:{}", comments, count);
        return new PageResult(page, pagesize, count, comments);
    }

    @Override
    public List<Movement> getMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        log.info("page:{},pagesize{},uid:{},state{}", page, pagesize, uid, state);
        Query query = new Query();
        if (!Objects.isNull(uid)) {
            query.addCriteria(Criteria.where("userId").is(uid));
        }
        if (!Objects.isNull(state)) {
            query.addCriteria(Criteria.where("state").is(state));
        }
        ;
        query.limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        log.info("查出的动态列表为{}", movements);
        return movements;
    }

    @Override
    public void update(String movementId, int state) {
        Query query = Query.query(Criteria.where("id").is(new ObjectId(movementId)));
        if (!"0".equals(state)) {
            log.info("状态码不为0,更新ing：{}", state);
            Update update = Update.update("state", state);
            mongoTemplate.updateFirst(query, update, Movement.class);
        } else {
            log.info("状态码为0,不更新：{}");
        }


    }

    @Override
    public void updateBatch(List<String> ids, int state) {
        Query query = Query.query(Criteria.where("id").in(ids.stream().map(s -> new ObjectId(s)).collect(Collectors.toList())));
        Query test = Query.query(Criteria.where("id").in(ids));
        //其实不要变，但是我就是喜欢炫耀技能
        log.info("testupdate{}", mongoTemplate.find(test, Movement.class));
        if (!"0".equals(state)) {
            log.info("状态码不为0,更新ing：{}", state);
            Update update = Update.update("state", state);
            mongoTemplate.updateMulti(query, update, Movement.class);
        }
    }
}
