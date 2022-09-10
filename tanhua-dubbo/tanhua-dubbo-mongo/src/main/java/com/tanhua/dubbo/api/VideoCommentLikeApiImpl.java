package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.VideoComment;
import com.tanhua.model.mongo.VideoCommentLike;
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
 * @Description: 视频评论 点赞数据层实现类
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@DubboService
@Slf4j
public class VideoCommentLikeApiImpl implements VideoCommentLikeApi {


    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 确认是否点赞了评论
     * @param currentUserId
     * @param commentId
     * @return
     */
    @Override
    public Boolean commentHasLike(Long currentUserId, String commentId) {
        Query query = Query.query(Criteria.where("userId").is(currentUserId).and("likeCommentId").is(new ObjectId(commentId)));
        return mongoTemplate.exists(query, VideoCommentLike.class);
    }


    /**
     * 视频评论点赞保存
     * @param videoCommentLike
     * @return
     */
    @Override
    public Integer saveAndReturnCounts(VideoCommentLike videoCommentLike) {
        //先保存视频评论点赞记录
        mongoTemplate.save(videoCommentLike);

        //下面对该视频的评论表中增加点赞数量
        //Query
        Query query = Query.query(Criteria.where("id").is(videoCommentLike.getLikeCommentId()));

        //Update
        Update update = new Update().inc("likeCount", 1);
        log.info("视频评论点赞数+1");
        //option
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        VideoComment modify = mongoTemplate.findAndModify(query, update, options, VideoComment.class);
        log.info("修改后的视频评论状态--modify:{}",modify);
        return modify.getLikeCount();
    }

    /**
     * 视频评论点赞删除
     * @param currentUserId
     * @param commentId
     * @return
     */
    @Override
    public Integer removeAndReturnCounts(Long currentUserId, String commentId) {
        //删除视频评论点赞记录
        Query query = Query.query(Criteria.where("likeCommentId").is(new ObjectId(commentId)).and("userId").is(currentUserId));
        mongoTemplate.remove(query, VideoCommentLike.class);

        //Query
        Query query1 = Query.query(Criteria.where("id").is(commentId));
        //Update
        Update update = new Update().inc("likeCount", -1);
        log.info("视频评论点赞数-1");
        //option
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        VideoComment modify = mongoTemplate.findAndModify(query1, update, options, VideoComment.class);
        log.info("修改后的视频评论状态modify:{}", modify);
        return modify.getLikeCount();
    }
}
