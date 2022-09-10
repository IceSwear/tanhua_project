package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 环信服务
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@Service
@Slf4j
public class HuanxinService {
    @DubboReference
    private UserApi userApi;


    /**
     * 获取换新信息
     * @return
     */
    public HuanXinUserVo findHuanxinUser() {

        Long userId = UserHolder.getUserId();
        User user = userApi.findById(userId);
        if (Objects.isNull(user)) {
            return null;
        }
        log.info("查询环信用户User:{}", user);
        return new HuanXinUserVo(user.getHxUser(), user.getHxPassword());
    }
}
