package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.AliyunOssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.ActionType;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.enums.FreezingRangeType;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.mongo.VideoComment;
import com.tanhua.model.mongo.VideoCommentLike;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 短视频服务  small videos service
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Service
@Slf4j
public class SmallVideosService {

    @Autowired
    private UserFreezeService userFreezeService;
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AliyunOssTemplate aliyunOssTemplate;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private VideoCommentApi videoCommentApi;
    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private FocusUserApi focusUserApi;

    @DubboReference
    private VideoCommentLikeApi videoCommentLikeApi;

    /**
     * upload videos 上传视频（数据库保存视频）
     *
     * @param videoThumbnail
     * @param videoFile
     */
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        Long currentUserId = UserHolder.getUserId();
        //check freezing status
        userFreezeService.checkUserStatus(FreezingRangeType.BAN_PUBLISH.getType(), currentUserId);
        //judge if either of them is null
        if (videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            throw new BusinessException(ErrorResult.error());
        }
        //upload the videosFile on fastDFS
        String originalFilename = videoFile.getOriginalFilename();
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), suffixName, null);
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
        //upload the imge on ali oss ,get url
        String imageUrl = aliyunOssTemplate.uploadFile(videoThumbnail);
        log.info("视频地址：{},封面地址：{}", videoUrl, imageUrl);
        //build video object
        Video video = new Video();
        video.setUserId(currentUserId);
        video.setVideoUrl(videoUrl);
        video.setPicUrl(imageUrl);
        video.setText("山外青山楼外楼，唱,跳,Rap,打篮球!");
        //save video well，return url name to check if upload successfully
        String videoId = videoApi.save(video);
        if (StringUtils.isEmpty(videoId.isEmpty())) {
            throw new BusinessException(ErrorResult.error());
        }
        //MQ发送记录存入发布video的日志,v
        mqMessageService.sendLogService(currentUserId, ActionType.PUBLISH_VIDEO.getType(), Constants.QUEUE_KEY_VIDEO, videoId);
    }


    /**
     * 视频列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @Cacheable(value = "videos", key = "T(com.tanhua.server.interceptor.UserHolder).userId+'_'+#page+'_'+#pagesize")
    public PageResult getVideoList(Integer page, Integer pagesize) {
        Long currentUserId = UserHolder.getUserId();
        //TODO query recommend vids in redis,从redis中获取推荐视频（这个可以写定时任务）定时保存 删除 ,",隔开",推荐的是pid注意了
        String redisKey = Constants.VIDEOS_RECOMMEND + currentUserId;
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        List<Video> list = new ArrayList<>();
        //初始化 设redis的Pages位0
        int redisPages = 0;
        if (!StringUtils.isEmpty(redisValue)) {
            //将值分割为数组
            String[] values = redisValue.split(",");
            //如果推荐的视频数量大于分页的偏移量offset长度，则直接将推荐的进行分页处理
            if ((page - 1) * pagesize < values.length) {
                //将数组转为流 分页并map重新构造想要的集合
                List<Long> vids = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize).map(e -> Long.valueOf(e)).collect(Collectors.toList());
                list = videoApi.findVideosByVids(vids);
                //TODO 这里可能会有一个bug，查出来没有这么多这个推荐视频,那list也是0,这意味着，我确实差了五页，但是，其实推荐视频挺鸡肋的
            }
            log.info("redis推荐视频{}:", list);
            //总数,每页数，返回总页数
            redisPages = PageUtil.totalPage(values.length, pagesize);
        }
        //如果推荐视频集合是空的,则，这里其实是差几页
        if (list.isEmpty()) {
            list = videoApi.queryVideoList(page - redisPages, pagesize);
        }
        //judge if null
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds, new UserInfo());
        List<VideoVo> vos = new ArrayList<>();
        for (Video video : list) {
            UserInfo userInfo = userInfoMap.get(video.getUserId());
            if (!Objects.isNull(userInfo)) {
                VideoVo vo = VideoVo.init(userInfo, video);
                //redis key hashkey
                String key = Constants.VIDEOS_INTERACT_KEY + video.getId().toHexString();
                //点赞视频
                String hashKey_Like = Constants.VIDEO_LIKE_HASHKEY + currentUserId;
                //这里是确认点赞和关注状态的,存在就位1，不存在就位0 相应的 要在点赞和关注那里也这样设计
                //判断是否已点赞,
                if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Like)) {
                    vo.setHasLiked(1);
                }
                //判断是否已关注
                //TODO 关注，这里对的是人 注意！目前的人+现有的用户id，其实相互关注应该也是喜欢，不过不做逻辑了
                if (stringRedisTemplate.opsForHash().hasKey(Constants.FOCUS_USER_KEY + currentUserId, video.getUserId().toString())) {
                    log.info("用户已关注");
                    vo.setHasFocus(1);
                }
                //if (!Objects.isNull(o) && o.contains(video.getId().toHexString())) {
                //log.info("用户已喜欢");
                //vo.setHasLiked(1);}
                vos.add(vo);
            }
        }
        return new PageResult(page, pagesize, Long.valueOf(vos.size()), vos);
    }


    /**
     * 关注用户 follow sb
     *
     * @param uid
     */
    public void followUser(Long uid) {
        Long currentUserId = UserHolder.getUserId();
        //judge if current user already follow user,存在关注用户表里
        focusUserApi.save(currentUserId, uid);
        //save in redis 保存在redis里
        String redisKey = Constants.FOCUS_USER_KEY + currentUserId;
        String hashKey = uid.toString();
        //结构   关注用户key---现有用户（关注用户id ---1）
        stringRedisTemplate.opsForHash().put(redisKey, hashKey, "1");
    }

    /**
     * 取消关注 unfollow sb
     *
     * @param uid
     */
    public void unfollowUser(Long uid) {
        Long currentUserId = UserHolder.getUserId();
        //删除关注列表
        focusUserApi.remove(currentUserId, uid);
        //删除redis里的关注信息
        String redisKey = Constants.FOCUS_USER_KEY + currentUserId;
        String hashKey = uid.toString();
        stringRedisTemplate.opsForHash().delete(redisKey, hashKey);
    }


    /**
     * 视频点赞
     *
     * @param videoId
     * @return
     */
    public Integer likeVideo(String videoId) {
        //get current user id
        Long currentUserId = UserHolder.getUserId();
        //check if user like点赞 already
        Boolean haslike = videoCommentApi.hasComment(videoId, currentUserId, CommentType.LIKE);
        if (haslike) {
            //点赞过就抛异常
            throw new BusinessException(ErrorResult.likeError());
        }
        //if not like before just save,没点赞过就保存点赞行为
        VideoComment videoComment = new VideoComment();
        videoComment.setCommentType(CommentType.LIKE.getType());
        videoComment.setPublishId(new ObjectId(videoId));
        videoComment.setUserId(currentUserId);
        videoComment.setCreated(System.currentTimeMillis());
        //
        log.info("点赞视频信息:{}", videoComment);
        //保存，并返回点赞数
        Integer counts = videoCommentApi.saveAndReturnCounts(videoComment);
        //redis存入点赞信息
        String key = Constants.VIDEOS_INTERACT_KEY + videoId;
        String hashKey = Constants.VIDEO_LIKE_HASHKEY + currentUserId;
        stringRedisTemplate.opsForHash().put(key, hashKey, videoId);

        //MQ发送记录存入点赞video的行为日志
        mqMessageService.sendLogService(currentUserId, ActionType.LIKE_VIDEO.getType(), Constants.QUEUE_KEY_VIDEO, videoId);
        log.info("当前like数据:{}", counts);
        return counts;
    }

    /**
     * 视频取消点赞
     *
     * @param videoId
     * @return
     */
    public Integer dislikeVideo(String videoId) {
        //get current user id
        Long currentUserId = UserHolder.getUserId();
        //check if user like点赞 already
        Boolean haslike = videoCommentApi.hasComment(videoId, currentUserId, CommentType.LIKE);
        if (!haslike) {
            //点赞过就抛异常
            throw new BusinessException(ErrorResult.disLikeError());
        }

        VideoComment comment = new VideoComment();
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setPublishId(new ObjectId(videoId));
        comment.setUserId(currentUserId);
        //删除,这里构造条件的对象
        Integer likeCounts = videoCommentApi.remove(comment);
        String key = Constants.VIDEOS_INTERACT_KEY + videoId;
        String hashKey = Constants.VIDEO_LIKE_HASHKEY + currentUserId;
        //redis里删除
        stringRedisTemplate.opsForHash().delete(key, hashKey);
        //MQ发送记录存入取消点赞video的日志
        mqMessageService.sendLogService(currentUserId, ActionType.DISLIKE_VIDEO.getType(), Constants.QUEUE_KEY_VIDEO, videoId);
        return likeCounts;
    }


    /**
     * 保存视频评论
     *
     * @param videoId
     * @param comment
     */

    public void saveVideoComment(String videoId, String comment) {
        Long currentUserId = UserHolder.getUserId();
        //构造视频评论
        userFreezeService.checkUserStatus(FreezingRangeType.BAN_COMMENT.getType(), currentUserId);
        //

        VideoComment videoComment = new VideoComment();
        videoComment.setPublishId(new ObjectId(videoId));
        videoComment.setCommentType(CommentType.COMMENT.getType());
        videoComment.setCreated(System.currentTimeMillis());
        videoComment.setContent(comment);
        videoComment.setUserId(currentUserId);
        Integer commentCount = videoCommentApi.saveAndReturnCounts(videoComment);
        //MQ发送记录存入评论video的日志
        mqMessageService.sendLogService(currentUserId, ActionType.COMMENT_VIDEO.getType(), Constants.QUEUE_KEY_VIDEO, videoId);
        log.info("commentCount:{}", commentCount);
    }


    /**
     * 对视频评论进行点赞
     *
     * @param commentId
     * @return
     */
    public Integer likeCommentOfVideo(String commentId) {
        Long currentUserId = UserHolder.getUserId();
        //查看是否点赞了评论
        Boolean hasLike = videoCommentLikeApi.commentHasLike(currentUserId, commentId);
        //已经点赞-true，发异常
        if (hasLike) {
            throw new BusinessException(ErrorResult.likeError());
        }
        //没点赞，记录，在2个表:点赞表记录点赞状态，评论表记录评论点赞数
        VideoCommentLike videoCommentLike = new VideoCommentLike();
        videoCommentLike.setLikeCommentId(new ObjectId(commentId));
        videoCommentLike.setCreated(System.currentTimeMillis());
        videoCommentLike.setUserId(currentUserId);
        Integer likeCounts = videoCommentLikeApi.saveAndReturnCounts(videoCommentLike);
        log.info("最新评论点赞数：{}", likeCounts);
        return likeCounts;
    }


    /**
     * 取消点赞视频评论
     *
     * @param commentId
     * @return
     */
    public Integer dislikeCommentOfVideo(String commentId) {
        Long currentUserId = UserHolder.getUserId();
        Boolean hasLike = videoCommentLikeApi.commentHasLike(currentUserId, commentId);
        //没点赞，发异常
        if (!hasLike) {
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //点赞，记录，在2个表中改，一个删 一个-1
        Integer likeCounts = videoCommentLikeApi.removeAndReturnCounts(currentUserId, commentId);
        log.info("最新评论点赞数：{}", likeCounts);
        return likeCounts;
    }

    /**
     * 视频评论列表
     *
     * @param videoId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult getVideoCommentsList(String videoId, Integer page, Integer pagesize) {
        //查询list
        PageResult pr = videoCommentApi.findByVideoId(videoId, page, pagesize);
        List<VideoComment> list = (List<VideoComment>) pr.getItems();
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds, new UserInfo());
        List<CommentVo> vos = new ArrayList<>();
        for (VideoComment videoComment : list) {
            UserInfo userInfo = userInfoMap.get(videoComment.getUserId());
            if (!Objects.isNull(userInfo)) {
                CommentVo vo = CommentVo.init(userInfo, videoComment);
                //这里也可以用reids
                if (videoCommentLikeApi.commentHasLike(UserHolder.getUserId(), videoComment.getId().toHexString())) {
                    vo.setHasLiked(1);
                }
                vos.add(vo);
            }
        }
        pr.setItems(vos);
        log.info("评论分页vos:{}", vos);
        return pr;
    }
}
