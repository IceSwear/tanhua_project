package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.expcetion.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @Description: 百度更新地理位置服务
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@Service
public class BaiduService {


    @DubboReference
    private UserLocationApi userLocationApi;

    /**
     * update location 更新地理位置
     * @param longitude
     * @param latitude
     * @param address
     */
    public void updateLocation(Double longitude, Double latitude, String address) {
        Boolean update = userLocationApi.updateLocation(UserHolder.getUserId(), longitude, latitude, address);
        if (!update) {
            throw new BusinessException(ErrorResult.error());
        }
    }
}
