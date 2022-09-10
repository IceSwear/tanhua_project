package com.tanhua.server.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.Announcement;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.*;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Description: query message 查询信息
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@Service
@Slf4j
public class MessageService {

    @DubboReference
    private UserApi userApi;
    @DubboReference
    private UserInfoApi userInfoApi;
    @Autowired
    private HuanXinTemplate huanXinTemplate;
    @DubboReference
    private FriendApi friendApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserLikeApi userLikeApi;
    @DubboReference
    private AnnouncementApi announcementApi;

    /**
     * query userInfo by Huanxin info 根据环信id查询用户详细
     *
     * @param huanxinId
     * @return
     */
    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        //use remote dubbo api to get user via huanxinId
        //使用dubbo Api远程调用-根据环信ID进行查询
        User user = userApi.findByHuanxinId(huanxinId);
        if (Objects.isNull(user)) {
            return null;
        }
        Long id = user.getId();
        //use remote dubbo api to get uerInfo after getting user by userId
        //调用dubbo远程api服务——根据userId得到userInfo
        UserInfo userInfo = userInfoApi.findById(id);
        //set a vo to seal the information
        //封装vo
        UserInfoVo vo = new UserInfoVo();
        //copy properties of user by hu-tool
        //用hutool拷贝属性
        BeanUtil.copyProperties(userInfo, vo);
        Integer age = userInfo.getAge();
        //judge if age variable is null, if not,save in vo
        //判断年龄字段是否为空，非空则保存在vo
        if (!Objects.isNull(age)) {
            //注意：uerInfo 和 userInfoVo的对象一样
            vo.setAge(age.toString());
        }
        return vo;
    }

    /**
     * add friends 添加好友关系
     *
     * @param friendId
     */
    public void contacts(Long friendId) {
        //get current user id from thread local
        //从thread local里得到现有的客户
        Long currentUserId = UserHolder.getUserId();
        // register relationship in Huanxin;将好友注册到环信
        Boolean addContact = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + currentUserId, Constants.HX_USER_PREFIX + friendId);
        //if fail to register ,throw exception
        //如果注册失败，抛异常
        if (!addContact) {
            throw new BusinessException(ErrorResult.error());
        }
        //if register successfully,save the relationship in Mongo
        //如果注册成功，则将关系存在mongo 里，collection：mongo
        friendApi.saveRelationship(currentUserId, friendId);
    }

    /**
     * get pr 得到好友/联系人pr结果
     *
     * @param keyword
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult getContacts(String keyword, Integer page, Integer pagesize) {
        Long currentUserId = UserHolder.getUserId();
        //查询出好友表
        PageResult pr = friendApi.findPageReusltById(currentUserId, page, pagesize);
        List<Friend> lists = (List<Friend>) pr.getItems();
        if (CollUtil.isEmpty(lists)) {
            return new PageResult();
        }

        List<Long> friendIds = CollUtil.getFieldValues(lists, "friendId", Long.class);
        //set condition
        UserInfo conditionInfo = new UserInfo();
        conditionInfo.setNickname(keyword);
        Map<Long, UserInfo> map = userInfoApi.findByIds(friendIds, conditionInfo);
        List<ContactVo> vos = new ArrayList<>();
        for (Friend friend : lists) {
            UserInfo userInfo = map.get(friend.getFriendId());
            if (!Objects.isNull(userInfo)) {
                ContactVo vo = ContactVo.init(userInfo);
                vos.add(vo);
            }
        }
        pr.setItems(vos);
        return pr;
    }

    /**
     * remove dislike in huanxin & collection of friend in Mongo
     *
     * @param unlikeUserId
     */
    public void remove(Long unlikeUserId) {
        Long currentUserId = UserHolder.getUserId();
        Boolean deleted = huanXinTemplate.deleteContact(Constants.HX_USER_PREFIX + currentUserId, Constants.HX_USER_PREFIX + unlikeUserId);
        if (!deleted) {
            throw new BusinessException(ErrorResult.error());
        }
        friendApi.remove(currentUserId, unlikeUserId);
    }

    /**
     * Query love list 消息推送-喜欢列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult loveList(Integer page, Integer pagesize) {
        //query by CommentType and userId,to get result
        PageResult pr = movementApi.findByPublishUserIdAndCommentType(UserHolder.getUserId(), CommentType.LOVE, page, pagesize);
        List<Comment> comments = (List<Comment>) pr.getItems();
        log.info("喜欢我的列表:{}", comments);
        return getCommentTypePageResult(pr, comments);
        //get who makes the comments
//        if (CollUtil.isEmpty(comments)) {
//            return new PageResult();
//        }
//        List<Long> userIds = CollUtil.getFieldValues(comments, "userId", Long.class);
//        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds, new UserInfo());
//        List<CommentTypeListVo> vos = new ArrayList<>();
//        //go through every user like list to build UserLove VO
//        // 遍历每一个userlike构造
//        for (Comment comment : comments) {
//            UserInfo userInfo = userInfoMap.get(comment.getUserId());
//            //判空
//            if (!Objects.isNull(userInfo)) {
//                CommentTypeListVo vo = CommentTypeListVo.init(userInfo);
//                vo.setCreateDate(new DateTime(comment.getCreated()).toString("YYYY-MM-dd HH:mm"));
//                vos.add(vo);
//            }
//        }
//        log.info("喜欢vos:{}", vos);
//        pr.setItems(vos);

    }

    /**
     * 消息推送-点赞列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult likeList(Integer page, Integer pagesize) {
        //query by CommentType and userId,to get result
        PageResult pr = movementApi.findByPublishUserIdAndCommentType(UserHolder.getUserId(), CommentType.LIKE, page, pagesize);
        List<Comment> comments = (List<Comment>) pr.getItems();
        log.info("点赞我的列表:{}", comments);
        //get who makes the comments
        return getCommentTypePageResult(pr, comments);
    }

    /**
     * 消息推送-评论列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult commentList(Integer page, Integer pagesize) {
        //query by CommentType and userId,to get result
        PageResult pr = movementApi.findByPublishUserIdAndCommentType(UserHolder.getUserId(), CommentType.COMMENT, page, pagesize);
        List<Comment> comments = (List<Comment>) pr.getItems();
        log.info("评论我的列表:{}", comments);
        //get who makes the comments
        return getCommentTypePageResult(pr, comments);
    }


    /**
     * 公共部分抽取优化，comment类的优化
     *
     * @param pageResult
     * @param comments
     * @return
     */
    private PageResult getCommentTypePageResult(PageResult pageResult, List<Comment> comments) {
        if (CollUtil.isEmpty(comments)) {
            return new PageResult();
        }
        List<Long> userIds = CollUtil.getFieldValues(comments, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds, new UserInfo());
        List<CommentTypeListVo> vos = new ArrayList<>();
        //go through every user like list to build UserLove VO
        // 遍历每一个userlike构造
        for (Comment comment : comments) {
            UserInfo userInfo = userInfoMap.get(comment.getUserId());
            if (!Objects.isNull(userInfo)) {
                CommentTypeListVo vo = CommentTypeListVo.init(userInfo);
                //时间要这里修改，因为一个评论一个时间 或者将上面的comment入参，形式不同，可灵活更具需求
                vo.setCreateDate(new DateTime(comment.getCreated()).toString("YYYY-MM-dd HH:mm"));
                vos.add(vo);
            }

        }
        pageResult.setItems(vos);
        return pageResult;
    }




    /**
     *  获取公告
     * @param page
     * @param pagesize
     * @return
     */
    @Cacheable(value = "announcement", key = "T(com.tanhua.server.interceptor.UserHolder).userId+'_'+#page+'_'+#pagesize")
    public PageResult getAnnouncements(Integer page, Integer pagesize) {
        PageResult pr = announcementApi.listAccountments(page, pagesize);
        List<Announcement> list = (List<Announcement>) pr.getItems();
        if (CollUtil.isEmpty(list)) {
            return new PageResult();
        }
        List<AnnouncementVo> vos = new ArrayList<>();
        for (Announcement announcement : list) {
            AnnouncementVo vo = new AnnouncementVo();
            if (!Objects.isNull(announcement)) {
                BeanUtil.copyProperties(announcement, vo);
            }
            //复制创建时间
            vo.setCreateDate(new DateTime(announcement.getCreated()).toString("YYYY-MM-dd HH:mm"));
            vos.add(vo);
        }
        log.info("公告分页查询结果vos:{},page:{},pagesize:{}", vos, page, pagesize);
        pr.setItems(vos);
        return pr;
    }
}
