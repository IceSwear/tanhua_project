package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.CommentLike;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/17
 */
@DubboService
@Slf4j
public class CommentApiImpl implements CommentApi {

    @Autowired
    MongoTemplate mongoTemplate;


    /**
     * publish and save comments then get the comment count
     * 发布评论并获取评论数量
     *
     * @param comment
     * @return
     */
    @Override
    public Integer save(Comment comment) {
        //query mvm 查询动态
//        Query query = new Query(Criteria.where("id").is(comment.getPublishId()));
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        log.info("movment属性：{}", movement);
        //judge if it is null
        if (!Objects.isNull(movement)) {
            //把动态Id附到movement
            comment.setPublishUserId(movement.getUserId());
        }
        log.info("comment属性：{}", comment);
        mongoTemplate.save(comment);
        //here we need to build 4 variables to find what we need
        //这里我们要构造4个属性去找到返回的内容

        //query
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));

        //update
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", 1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", 1);
        } else {
            update.inc("loveCount", 1);
        }

        //option
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);

        //then to conduct the method
        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        log.info("modify-movement:{}", modify);
        //then judge return type
//        if (comment.getCommentType() == CommentType.LIKE.getType()) {
//            return modify.getLikeCount();
//        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
//            return modify.getCommentCount();
//        } else {
//            return modify.getLoveCount();
//        }

        //just use a method to do this rather than above直接用封装的方法
        return modify.statisCount(comment.getCommentType());
    }

    @Override
    public PageResult getComments(String movementId, CommentType commentType, Integer page, Integer pagesize) {
        //build a query,condition: publishId =movementId;commentType=ommentType.getType(),skip .limit and make an oder rule!
        //构造查询条件，2个等于条件，分页信息，还有排序规则
        //前端页面有bug有缓存
        Query query = new Query(Criteria.where("publishId").is(new ObjectId(movementId)).and("commentType").is(commentType.getType())).with(Sort.by(Sort.Order.desc("created")));
        long count = mongoTemplate.count(query, Comment.class);
        query.skip((page - 1) * pagesize).limit(pagesize);
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        //
        log.info("分页查询评论:couts:{},comments:{}", count, comments);
        return new PageResult(page, pagesize, count, comments);
    }


    /**
     * judge if commentType comment exit 判断like评论是否存在
     *
     * @param movementId
     * @param userId
     * @param commentType
     * @return
     */
    @Override
    public Boolean hasComment(String movementId, Long userId, CommentType commentType) {

        Query query = new Query(Criteria.where("publishId").is(new ObjectId(movementId)).and("commentType").is(commentType.getType()).and("userId").is(userId));
//        Comment comment = mongoTemplate.findOne(query, Comment.class);
//        if (!Objects.isNull(comment)) {
//            return true;
//        } else {
//            return false;
//        }
        return mongoTemplate.exists(query, Comment.class);
    }

    /**
     * remove comment 删除评论
     *
     * @param comment
     * @return
     */
    @Override
    public Integer remove(Comment comment) {
        //set query condition to remove current comment
        //设置查询条件，删除现有评论
        Query query = new Query(Criteria.where("publishId").is(comment.getPublishId()).and("commentType").is(comment.getCommentType()).and("userId").is(comment.getUserId()));
        mongoTemplate.remove(query, Comment.class);
        //query
        Query movementQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        //update
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else {
            update.inc("loveCount", -1);
        }
        //option
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        //then to conduct the method
        Movement modify = mongoTemplate.findAndModify(movementQuery, update, options, Movement.class);
        log.info("modify-movement:{}", modify);
        //then judge return type
//        if (comment.getCommentType() == CommentType.LIKE.getType()) {
//            return modify.getLikeCount();
//        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
//            return modify.getCommentCount();
//        } else {
//            return modify.getLoveCount();
//        }
        //just use a method to do this rather than above直接用封装的方法
        return modify.statisCount(comment.getCommentType());
    }


    /**
     * 获取评论列表
     *
     * @param page
     * @param pagesize
     * @param messageID
     * @return
     */
    @Override
    public PageResult getCommentList(Integer page, Integer pagesize, String messageID) {
        Query query = Query.query(Criteria.where("commentType").is(CommentType.COMMENT.getType()).and("publishId").is(new ObjectId(messageID)));
        long count = mongoTemplate.count(query, Comment.class);
        query.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        log.info("后台评论分页列表commnets:{}", comments);
        return new PageResult(page, pagesize, count, comments);
    }


    /**
     * 确认评论是否已喜欢
     * @param userId
     * @param commentId
     * @return
     */
    @Override
    public boolean commentHasLike(Long userId, String commentId) {
        Query query = Query.query(Criteria.where("userId").is(userId).and("likeCommentId").is(commentId));
        return mongoTemplate.exists(query, CommentLike.class) ;
    }
}

