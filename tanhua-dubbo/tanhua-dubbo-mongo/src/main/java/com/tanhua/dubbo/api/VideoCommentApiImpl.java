package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.mongo.VideoComment;
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
 * @Description: 视频评论 数据层实现类
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@DubboService
@Slf4j
public class VideoCommentApiImpl implements VideoCommentApi {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 评论类型是否存在
     *
     * @param videoId
     * @param currentUserId
     * @param commentType
     * @return
     */
    @Override
    public Boolean hasComment(String videoId, Long currentUserId, CommentType commentType) {
        //发布的id是videid 评论类型+用户id是现有id
        Query query = new Query(Criteria.where("publishId").is(new ObjectId(videoId)).and("commentType").is(commentType.getType()).and("userId").is(currentUserId));
        return mongoTemplate.exists(query, VideoComment.class);
    }


    /**
     * 保存视频评论类型并返回最新的数量
     *
     * @param videoComment
     * @return
     */
    @Override
    public Integer saveAndReturnCounts(VideoComment videoComment) {
        //根据发布id找到视频
        Video video = mongoTemplate.findById(videoComment.getPublishId(), Video.class);
        log.info("video属性：{}", video);
        if (!Objects.isNull(video)) {
            //视频不为空，就把视频发布人id保存进来
            videoComment.setPublishUserId(video.getUserId());
        }

        //保存发布,发布评论 第一张表
        log.info("videoComment属性:{}", videoComment);
        mongoTemplate.save(videoComment);


        //query,第二张表
        Query query = Query.query(Criteria.where("id").is(videoComment.getPublishId()));

        //update
        Update update = new Update();
        if (videoComment.getCommentType().equals(CommentType.LIKE.getType())) {
            //点赞数+1
            log.info("点赞数+1");
            update.inc("likeCount", 1);
        } else if (videoComment.getCommentType().equals(CommentType.COMMENT.getType())) {
            log.info("评论数+1");
            update.inc("commentCount", 1);
        } else {
            log.info("其实没有loveCount，这是冗余字段,因为这里视频前段只有评论和喜欢2种");
            update.inc("loveCount", 1);
        }
        //option
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Video modify = mongoTemplate.findAndModify(query, update, options, Video.class);
        log.info("modify-video:{}", modify);
        //返回数量
        return modify.statisCount(videoComment.getCommentType());
    }

    @Override
    public Integer remove(VideoComment comment) {
        Query query = new Query(Criteria.where("publishId").is(comment.getPublishId()).and("commentType").is(comment.getCommentType()).and("userId").is(comment.getUserId()));
        mongoTemplate.remove(query, VideoComment.class);
        Query videoQuery = Query.query(Criteria.where("id").is(comment.getPublishId()));
        //update
        Update update = new Update();
        if (comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount", -1);
        } else if (comment.getCommentType() == CommentType.COMMENT.getType()) {
            update.inc("commentCount", -1);
        } else {
            log.info("其实没有loveCount，这是冗余字段,因为这里视频前段只有评论和喜欢2种");
            update.inc("loveCount", -1);
        }
        //option
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        //then to conduct the method
        Video modify = mongoTemplate.findAndModify(videoQuery, update, options, Video.class);
        log.info("modify-video:{}", modify);
        return modify.statisCount(comment.getCommentType());
    }


    /**
     * 根据视频id对评论进行分页
     *
     * @param videoId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findByVideoId(String videoId, Integer page, Integer pagesize) {
        log.info("mongo-入参视频id:{},page:{},pagesize:{}", videoId, page, pagesize);
        Query query = Query.query(Criteria.where("publishId").is(new ObjectId(videoId)).and("commentType").is(CommentType.COMMENT.getType()));
        //先统计
        long count = mongoTemplate.count(query, VideoComment.class);
        //再分页
        query.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<VideoComment> list = mongoTemplate.find(query, VideoComment.class);
        log.info("视频评论:{}", list);
        return new PageResult(page, pagesize, count, list);
    }
}
