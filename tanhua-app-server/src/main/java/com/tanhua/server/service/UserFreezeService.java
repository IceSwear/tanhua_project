package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.expcetion.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @Description: 用户冻结服务
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Service
public class UserFreezeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 用户冻结确认
     *
     * @param state
     * @param userId
     */
    public void checkUserStatus(String state, Long userId) {
        //默认状态1 即为正常 ，2不正常 n
        //get key
        String redisKey = Constants.USER_FREEZE + userId;
        String value = stringRedisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isEmpty(value)) {
            //不为空
            Map map = JSON.parseObject(value, Map.class);
            //TODO 冻结范围，1为冻结登录，2为冻结发言，3为冻结发布动态
            String freezingRange = (String) map.get("freezingRange");
            if (state.equals(freezingRange)) {
                //一样，说明冻结了？
                throw new BusinessException(ErrorResult.builder().errMessage("用户被冻结").build());
            }
        }


    }
}
