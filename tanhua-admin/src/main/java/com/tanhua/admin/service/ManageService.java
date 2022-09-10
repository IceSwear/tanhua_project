package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description: 管理服务 management service
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Service
@Slf4j
public class ManageService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private CommentApi commentApi;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 用户列表分页
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult getUserList(Integer page, Integer pagesize) {
        IPage iPage = userInfoApi.findAll(page, pagesize);
        List<UserInfo> records = iPage.getRecords();
//        for (UserInfo userInfo : records) {
//            if (stringRedisTemplate.hasKey(Constants.USER_FREEZE+userInfo.getId())){
//                userInfo.setUserStatus("2");
//            }
//        }
        //将状态也显示出来
        List<UserInfo> collect = records.stream().map(s -> {
            if (stringRedisTemplate.hasKey(Constants.USER_FREEZE + s.getId())) {
                s.setUserStatus(Constants.FREEZE_USER);
            }
            return s;
        }).collect(Collectors.toList());
        log.info("collect:{}", collect);
        return new PageResult(page, pagesize, iPage.getTotal(), collect);
    }

    public UserInfo getUserInfo(Long id) {
        UserInfo userInfo = userInfoApi.findById(id);
        if (stringRedisTemplate.hasKey(Constants.USER_FREEZE + id)) {
            userInfo.setUserStatus(Constants.FREEZE_USER);
        }
        return userInfo;
    }

    public PageResult getVideosList(Long userId, Integer page, Integer pagesize) {
        return videoApi.queryVideoListByUserId(userId, page, pagesize);
    }

    public PageResult getMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        PageResult pr = movementApi.findByUserId(uid, state, page, pagesize);
        List<Movement> lists = (List<Movement>) pr.getItems();
        if (CollUtil.isEmpty(lists)) {
            return new PageResult();
        }
        log.info("动态列表不为空:{}", lists);
        List<Long> userIds = CollUtil.getFieldValues(lists, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds, new UserInfo());
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : lists) {
            UserInfo userInfo = userInfoMap.get(movement.getUserId());
            if (!Objects.isNull(userInfo)) {
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        log.info("MovementVo列表为：{}", vos);
        pr.setItems(vos);
        return pr;
    }


    /**
     * 动态详情
     *
     * @param movementId
     * @return
     */
    public MovementsVo getMovementsDetails(String movementId) {
        Movement movement = movementApi.findById(movementId);
        UserInfo userInfo = userInfoApi.findById(movement.getUserId());
        MovementsVo vo = MovementsVo.init(userInfo, movement);
        return vo;
    }

    /**
     * 评论列表
     *
     * @param page
     * @param pagesize
     * @param messageID
     * @return
     */
    public PageResult commentList(Integer page, Integer pagesize, String messageID) {
        PageResult pr = commentApi.getCommentList(page, pagesize, messageID);
        List<Comment> lists = (List<Comment>) pr.getItems();
        if (CollUtil.isEmpty(lists)) {
            return new PageResult();
        }
        log.info("lists不为空,{}", lists);
        List<Long> userIds = CollUtil.getFieldValues(lists, "userId", Long.class);
        Map<Long, UserInfo> userInfoMap = userInfoApi.findByIds(userIds, new UserInfo());
        List<CommentVo> vos = new ArrayList<>();

        for (Comment comment : lists) {
            UserInfo userInfo = userInfoMap.get(comment.getUserId());
            if (!Objects.isNull(userInfo)) {
                CommentVo vo = CommentVo.init(userInfo, comment);
                vos.add(vo);
            }
        }
        pr.setItems(vos);
        return pr;
    }

    public Map freeze(Map map) {
        log.info("冻结map:{}", map);
        Integer userId = Integer.valueOf(map.get("userId").toString());
        Integer freezingTime = Integer.valueOf(map.get("freezingTime").toString());
//        Integer freezingRange = (Integer) map.get("freezingRange");
//        String reasonsForFreezing = (String) map.get("reasonsForFreezing");
//        String frozenRemarks = (String) map.get("frozenRemarks");
        String redisKey = Constants.USER_FREEZE + userId;
        int days = 0;
        //1--3天  2 7天  3 forever
        if (freezingTime.equals(1)) {
            days = 3;
        } else if (freezingTime.equals(2)) {
            days = 7;
        }
        String value = JSON.toJSONString(map);
        //judge condition of days
        if (days > 0) {
            stringRedisTemplate.opsForValue().set(redisKey, value, days, TimeUnit.DAYS);
        } else {
            // permenent,为0就是永久
            stringRedisTemplate.opsForValue().set(redisKey, value);
        }
        //记号?\\
        Map retMap = new HashMap<>();
        retMap.put("message", "冻结成功!");
        return retMap;
    }

    public Map unfreeze(Map map) {
        log.info("解冻map:{}", map);
        Integer userId = Integer.valueOf(map.get("userId").toString());
        String redisKey = Constants.USER_FREEZE + userId;
        stringRedisTemplate.delete(redisKey);
        Map retMap = new HashMap<>();
        retMap.put("message", "解冻成功");
        return retMap;
    }

    public Map reject(List<String> ids) {
        //改动态状态 //状态 0：未审（默认），1：通过，2：驳回
        movementApi.updateBatch(ids, 2);
        Map retMap = new HashMap<>();
        retMap.put("message", "操作成功");
        return retMap;
    }

    public Map pass(List<String> ids) {
        //状态 0：未审（默认），1：通过，2：驳回
        movementApi.updateBatch(ids, 1);
        Map retMap = new HashMap<>();
        retMap.put("message", "操作成功");
        return retMap;
    }
}
