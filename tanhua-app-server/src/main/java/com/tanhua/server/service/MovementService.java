package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.AliyunOssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.ActionType;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
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
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Service
@Slf4j
public class MovementService {

    @Autowired
    private AliyunOssTemplate aliyunOssTemplate;


    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private FriendApi friendApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private CommentApi commentApi;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private VisitorsApi visitorsApi;

    @Autowired
    private MqMessageService mqMessageService;

    /**
     * 发布动态 publish mvm
     *
     * @param movement
     * @param imageContent
     * @throws IOException
     */
    public void publishMovement(Movement movement, MultipartFile imageContent[]) throws IOException {
        Long currentUserId = UserHolder.getUserId();
        //judge if the text of movement is null, 判断内容是否为空
        if (StringUtils.isEmpty(movement.getTextContent())) {
            // if it is empty
            throw new BusinessException(ErrorResult.contentError());
        }
        //set a list to contain the urls
        List<String> medias = new ArrayList();
        //then upload the imageContent one by one,逐个上传图片内容
        for (MultipartFile multipartFile : imageContent) {
            String url = aliyunOssTemplate.uploadFile(multipartFile);
            medias.add(url);
        }
        // set uploader and save the urls
        movement.setMedias(medias);
        movement.setUserId(currentUserId);
        String movementId = movementApi.saveMovement(movement);
        //发送队列
        log.info("准备发送队里的Id:{}", movementId);
        //审计发送审计
        mqMessageService.sendAuditService(movementId);
        //记录发布日志 busID是啥--业务Id，动态就是动态ID，视频就是视频ID
        mqMessageService.sendLogService(currentUserId, ActionType.PUBLISH_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
    }

    /**
     * 查询个人动态 query personal mvm
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @Cacheable(value = "my_mvm", key = "#userId+'_'+#page+'_'+#pagesize")
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        //查询个人动态
        PageResult pr = movementApi.findByUserId(userId,1,page, pagesize);
        //获取item
        List<Movement> items = (List<Movement>) pr.getItems();
        //judge if item is null
        return this.getFinalPageResult(items, pr);

        //----隔离
//        if (CollUtil.isEmpty(items)) {
//            return pr;
//        }
//        //if not null ,find by id to get current user Info(it will query via mysql)
//        UserInfo userInfo = userInfoApi.findById(userId);
//        List<MovementsVo> vos = new ArrayList<>();
//        for (Movement movement : items) {
//            MovementsVo vo = MovementsVo.init(userInfo, movement);
//            //完成，从缓存读取喜爱信息
//            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
//            String hashKey_Like = Constants.MOVEMENT_LIKE_HASHKEY + userId;
//            String hashKey_Love = Constants.MOVEMENT_LOVE_HASHKEY + userId;
//            //judge
//            if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Like)) {
//                vo.setHasLiked(1);
//            }
//            if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Love)) {
//                vo.setHasLoved(1);
//            }
//            vos.add(vo);
//        }
//        pr.setItems(vos);
//        return pr;
    }

    /**
     * 根据动态id查询
     *
     * @param movementId
     * @return
     */
    public MovementsVo findByMovementId(String movementId) {
        Long currentUserId = UserHolder.getUserId();
        //log browse mvm record记录浏览动态日志
        mqMessageService.sendLogService(currentUserId, ActionType.BROWSE_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
        //query movement collection via movementId
        Movement movement = movementApi.findById(movementId);
        //judge if movement is null
        if (!Objects.isNull(movement)) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
            String hashKey_Like = Constants.MOVEMENT_LIKE_HASHKEY + currentUserId;
            String hashKey_Love = Constants.MOVEMENT_LOVE_HASHKEY + currentUserId;
            //judge
            if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Like)) {
                vo.setHasLiked(1);
            }
            if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Love)) {
                vo.setHasLoved(1);
            }
            return vo;
        } else {
            //if it is null, just return null
            return null;
        }
    }


    /**
     * 查询好友动态list
     *
     * @param page
     * @param pagesize
     * @return
     */
    @Cacheable(value = "friends_mvm", key = "T(com.tanhua.server.interceptor.UserHolder).userId+'_'+#page+'_'+#pagesize")
    public PageResult queryMovementsOfFriends(Integer page, Integer pagesize) {
        //得到当前用户id
        Long userId = UserHolder.getUserId();
        //先判断是不是好友现在
        List<Long> friendIds = friendApi.getFriendsIds(userId);
        log.info("当前用户现有的好友;{}");
        //query friends' lists of mvm
        // 查询对应userId 朋友的动态列表,it's important!!
        PageResult pr = movementApi.findFriedMovementsList(page, pagesize, userId, friendIds);
        //judge if it is empty,return default pr object if yes
        // 判断是否为空，空则返回默认;
        List<Movement> items = (List<Movement>) pr.getItems();
        return this.getFinalPageResult(items, pr);
    }


    /**
     * 抽取出的方法
     *
     * @param list
     * @param pr
     * @return
     */
    private PageResult getFinalPageResult(List<Movement> list, PageResult pr) {

        if (CollUtil.isEmpty(list)) {
            return pr = new PageResult();
        }
        //if it is not empty ,to deal with it
        //若不为空，则开始处理 遍历
        //build a list of ids for mvm's list
        //构造一个动态userid的list
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //通过userId筛选出key-value 的map 方便后续取值
        //per userId to filter and get the map(k,v), so can be easier to get what you want
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, new UserInfo());
        //build a empty list of movementVo ,then to contain vo one by one
        //构建空的vo对象，遍历后得到vos集合
        List<MovementsVo> vos = new ArrayList<>();
        Long userId = UserHolder.getUserId();
        for (Movement movement : list) {
            //get userid of mvm
            //得到动态所属的用户id
            Long userId1 = movement.getUserId();
            UserInfo userInfo = map.get(userId1);
            //!这个就是喜欢和Love的情况！
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            String key = Constants.MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
            String hashKey_Like = Constants.MOVEMENT_LIKE_HASHKEY + userId;
            String hashKey_Love = Constants.MOVEMENT_LOVE_HASHKEY + userId;
            //judge
            if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Like)) {
                vo.setHasLiked(1);
            }
            if (stringRedisTemplate.opsForHash().hasKey(key, hashKey_Love)) {
                vo.setHasLoved(1);
            }
            vos.add(vo);
        }
        //将值重新附回
        pr.setItems(vos);
        return pr;
    }

    /**
     * 推荐动态,先查redis，redis没有再在mongo里补满10条
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        Long currentUserId = UserHolder.getUserId();
        //从redis中获取推荐数据
        String redisKey = Constants.MOVEMENTS_RECOMMEND + currentUserId;
        String redisValue = stringRedisTemplate.opsForValue().get(redisKey);
        //create an empty list, 创建一个空集合
        List<Movement> list = Collections.EMPTY_LIST;
        //judge if redisvalue is empty 判断redisvalue是否为空
        if (StringUtils.isEmpty(redisValue)) {
            // 为空就随机
            list = movementApi.randomMovements(pagesize,currentUserId);
        } else {
            //不为空就是查出来
            String[] split = redisValue.split(",");
            //judge
            if ((page - 1) * pagesize < split.length) {
                //数组的分页
                List<Long> pids = Arrays.stream(split).skip((page - 1) * pagesize).limit(pagesize).map(s -> Long.valueOf(s)).collect(Collectors.toList());
                list = movementApi.findMovementsByPids(pids);
            }
        }
        //返回的值为页数*页码 因为是推荐
        return this.getFinalPageResult(list, new PageResult(page, pagesize, Long.valueOf(page * pagesize), null));
    }


    /**
     * like mvm 动态点赞
     *
     * @param movementId
     * @return
     */
    public Integer like(String movementId) {
        //get user id
        Long userId = UserHolder.getUserId();
        //check if user liked mvm already是否已经点赞
        Boolean hasLike = commentApi.hasComment(movementId, userId, CommentType.LIKE);
        //if already liked ,throw exception that like error 如果点过赞了，就抛出异常
        if (hasLike) {
            throw new BusinessException(ErrorResult.likeError());
        }
        //if not liked, saved the liked status没点赞，那就构造点赞
        Comment comment = new Comment();
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setPublishId(new ObjectId(movementId));
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        //存入点赞并返回点赞数
        Integer counts = commentApi.save(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        stringRedisTemplate.opsForHash().put(key, hashKey, "1");
        //MQ发送记录喜欢动态ID日志 0203
        mqMessageService.sendLogService(userId, ActionType.LIKE_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
        return counts;
    }

    /**
     * dislike mvm
     *
     * @param movementId
     * @return
     */
    public Integer dislike(String movementId) {
        //get current user id 得到当前用户id
        Long userId = UserHolder.getUserId();
        //check if the comment exit
        //确认comment是否存在
        Boolean hasComment = commentApi.hasComment(movementId, userId, CommentType.LIKE);
        //throw dislike error exception if user didn't like
        if (!hasComment) {
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //if user liked alrady,use dubbo api to remove like comment and return the updated likeCounts at the same time
        //如果用户喜欢过，用dubbo远程调用api接口删除like的comment，与此同时返回更新的点赞数
        Comment comment = new Comment();
        comment.setCommentType(CommentType.LIKE.getType());
        comment.setPublishId(new ObjectId(movementId));
        comment.setUserId(userId);
        Integer likeCounts = commentApi.remove(comment);
        //set redis key and remove like status
        //构造redis的keys，删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + userId;
        stringRedisTemplate.opsForHash().delete(key, hashKey);
        //0206 mq发送记录取消点赞动态
        mqMessageService.sendLogService(userId, ActionType.DISLIKE_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
        return likeCounts;
    }

    public Integer unlove(String movementId) {
        //get current user id
        //得到当前用户id
        Long userId = UserHolder.getUserId();
        Boolean hasLove = commentApi.hasComment(movementId, userId, CommentType.LOVE);
        if (!hasLove) {
            throw new BusinessException(ErrorResult.disloveError());
        }
        Comment comment = new Comment();
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setPublishId(new ObjectId(movementId));
        comment.setUserId(userId);
        Integer likeCounts = commentApi.remove(comment);
        //set redis key and remove like status
        //构造redis的keys，删除点赞状态
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        stringRedisTemplate.opsForHash().delete(key, hashKey);
        //0207 mq发送记录取消喜欢动态行为
        mqMessageService.sendLogService(userId, ActionType.UNLOVE_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
        return likeCounts;
    }

    public Integer love(String movementId) {
        //get user id
        Long userId = UserHolder.getUserId();
        //check if user liked mvm already
        Boolean hasLove = commentApi.hasComment(movementId, userId, CommentType.LOVE);
        //if already liked ,throw exception that like error
        if (hasLove) {
            throw new BusinessException(ErrorResult.loveError());
        }
        //if not liked, saved the liked status
        Comment comment = new Comment();
        comment.setCommentType(CommentType.LOVE.getType());
        comment.setPublishId(new ObjectId(movementId));
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());
        Integer counts = commentApi.save(comment);
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + userId;
        stringRedisTemplate.opsForHash().put(key, hashKey, "1");
        //0204 mq发送记录喜欢动态行为
        mqMessageService.sendLogService(userId, ActionType.LOVE_MVM.getType(), Constants.QUEUE_KEY_MVM, movementId);
        return counts;
    }


    /**
     * visitors list 访客记录 用redis
     *
     * @return
     */
    public List<VisitorsVo> listVisitorsByUserId() {

        Long userId = UserHolder.getUserId();
        //build the key hashkey
        String key = Constants.VISITORS_USER;
        //当前用户
        String hashKey = String.valueOf(userId);
        //获取值
        String value = (String) stringRedisTemplate.opsForHash().get(key, hashKey);
        //时间转换
        Long queryTime = StringUtils.isEmpty(value) ? null : Long.valueOf(value);
        //TODO 得到5个最近的访客；get 4 latest user visited you,这里要注意，访客不能是自己
        List<Visitors> visitors = visitorsApi.getVisitorsListByIdAndQueryTime(queryTime, userId);
        // record the time in redis, so next time you will query more than this point,记录这次操作的时间
        //这个redis的时间是上次查看完整访客列表的时间
//        stringRedisTemplate.opsForHash().put(key, hashKey, String.valueOf(System.currentTimeMillis()));
        if (CollUtil.isEmpty(visitors)) {
            return new ArrayList<>();
        }
        //get ids list
        List<Long> visitorUserIds = CollUtil.getFieldValues(visitors, "visitorUserId", Long.class);

        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(visitorUserIds, new UserInfo());
        List<VisitorsVo> vos = new ArrayList<>();
        for (Visitors visitor : visitors) {
            UserInfo userInfo = userInfoMap.get(visitor.getVisitorUserId());
            if (!Objects.isNull(userInfo)) {
                VisitorsVo vo = VisitorsVo.init(userInfo, visitor);
                vos.add(vo);
            }
        }
        log.info("访客Vos为:{}", vos);
        return vos;
    }
}
