package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.CommentLikeApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.ActionType;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.CommentLike;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: comment service 评论服务
 * @Author: Spike Wong
 * @Date: 2022/8/17
 */
@Service
@Slf4j
public class CommentService {


    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private CommentLikeApi commentLikeApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private MqMessageService mqMessageService;

    /**
     * publish comment 发布评论(保存评论)
     *
     * @param movementId
     * @param comment
     */
    public void saveComments(String movementId, String comment) {
        //get currnet user id form the threadlocal——————从线程中获当前用户id
        Long currentUserId = UserHolder.getUserId();
        //build comment object  构造comment对象
        Comment comment1 = new Comment();
        comment1.setPublishId(new ObjectId(movementId));
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setContent(comment);
        comment1.setUserId(currentUserId);
        comment1.setCreated(System.currentTimeMillis());
        //user dubbo api to save comment 调用dubbo远程api保存评论
        Integer commentCount = commentApi.save(comment1);
        //MQ发送记录存入评论MVM的日志
        mqMessageService.sendLogService(currentUserId, ActionType.COMMENT_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
        log.info("commentCount:{}", commentCount);
    }

    /**
     * get comments list 获取评论列表(分页查询)
     *
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult getComments(String movementId, Integer page, Integer pagesize) {
        //use remote dubbo api to get list of comments of a movement,here we need to have movmenntId,CommentType,pageBean
        //使用dubbo 远程api得到该动态的list 评论对象，之类我们需要几个参数，comment id ,评论类型，pageBean
        PageResult pr = commentApi.getComments(movementId, CommentType.COMMENT, page, pagesize);
        List<Comment> list = (List<Comment>) pr.getItems();
        //judge the if list is empty 判断list是否为空
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        //if not ,start to build vo  如果非空，开始创建vo对象
        //use hutool to get userids via comment list, 使用hutool通过comment的list得到ids的list
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //get map of userInfo key is userid, value is userInfo,调用userInforApi得到 k-v对于的map
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, new UserInfo());
        //set a new empty list of Comment Vo 构造一个空的commentVo对象
        List<CommentVo> vos = new ArrayList<>();
        //loop list to add the single vo one by one to the empty vos list
        //循环list把单个bo挨个加到vos的list中
        for (Comment comment : list) {
            //judge
            UserInfo userInfo = map.get(comment.getUserId());
            if (!Objects.isNull(userInfo)) {
                log.info("useinfo不为空");
                CommentVo vo = CommentVo.init(userInfo, comment);
                //TODO   评论点赞数，是否喜欢
                boolean b = commentLikeApi.commentHasLike(UserHolder.getUserId(), comment.getId().toHexString());
                System.out.println(b);
                if (b) {
                    log.info("该评论已喜欢");
                    vo.setHasLiked(1);
                }
                vos.add(vo);

            }
        }
        log.info("评论VOs:{}",vos);
        pr.setItems(vos);
        return pr;
    }


    /**
     * 点赞评论  like comments
     *
     * @param commentId
     * @return
     */
    public Integer likeComment(String commentId) {
        Long currentUserId = UserHolder.getUserId();
        //query if liked alrady;
        Boolean isLike = commentLikeApi.commentHasLike(currentUserId, commentId);
        if (isLike) {
            throw new BusinessException(ErrorResult.likeError());
        }
        //if not like ,not we like
        Integer LikeCounts = commentLikeApi.save(new CommentLike(null, new ObjectId(commentId), currentUserId, System.currentTimeMillis()));
        log.info("最新评论点赞数：{}", LikeCounts);
        return LikeCounts;
    }

    /**
     * 动态评论取消点赞
     *
     * @param commentId
     * @return
     */
    public Integer dislikeComment(String commentId) {
        Long currentUserId = UserHolder.getUserId();
        Boolean hasLike = commentLikeApi.commentHasLike(currentUserId, commentId);
        //没点赞发异常
        if (!hasLike) {
            throw new BusinessException(ErrorResult.disLikeError());
        }
        Integer likeCounts = commentLikeApi.remove(currentUserId, commentId);
        log.info("最新动态评论点赞数：{}", likeCounts);
        return likeCounts;
    }
}
