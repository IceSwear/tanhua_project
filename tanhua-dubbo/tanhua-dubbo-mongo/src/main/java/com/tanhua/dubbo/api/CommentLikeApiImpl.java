package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.CommentLike;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @Description: 评论点赞服务实现类
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@DubboService
public class CommentLikeApiImpl implements CommentLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 评论是否已点赞
     *
     * @param currentUserId
     * @param commentId
     * @return
     */
    @Override
    public Boolean commentHasLike(Long currentUserId, String commentId) {
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("likeCommentId").is(new ObjectId(commentId)));
        return mongoTemplate.exists(query, CommentLike.class);
    }

    /**
     * 保存点赞喜欢
     *
     * @param commentLike
     * @return
     */
    @Override
    public Integer save(CommentLike commentLike) {
        mongoTemplate.save(commentLike);


        //query
        Query query = Query.query(Criteria.where("id").is(commentLike.getLikeCommentId()));

        //update
        Update update = new Update().inc("likeCount", 1);
        log.info("动态评论点赞数+1");
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Comment modify = mongoTemplate.findAndModify(query, update, options, Comment.class);
        return modify.getLikeCount();
    }

    /**
     * 动态评论取消点赞 删除
     *
     * @param currentUserId
     * @param commentId
     * @return
     */
    @Override
    public Integer remove(Long currentUserId, String commentId) {
        Query query = Query.query(Criteria.where("likeCommentId").is(new ObjectId(commentId)).and("userId").is(currentUserId));
        mongoTemplate.remove(query, CommentLike.class);


        //query
        Query updateQuery = Query.query(Criteria.where("id").is(new ObjectId(commentId)));

        //update
        Update update = new Update();
        update.inc("likeCount", -1);
        log.info("动态评论点赞数-1");
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Comment modify = mongoTemplate.findAndModify(updateQuery, update, options, Comment.class);
        return modify.getLikeCount();
    }
}
