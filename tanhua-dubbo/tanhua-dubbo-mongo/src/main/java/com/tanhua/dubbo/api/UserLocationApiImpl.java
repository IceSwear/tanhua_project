package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.UserLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Objects;

/**
 * @Description: UserLocationApi实现类
 * @Author: Spike Wong
 * @Date: 2022/8/18
 */
@DubboService
@Slf4j
public class UserLocationApiImpl implements UserLocationApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * search nearby 搜附近
     *
     * @param currentUserId
     * @param distance
     * @return
     */
    @Override
    public List<Long> queryNearUser(Long currentUserId, String distance) {
        //query to get current user's location
        //先查询当前用户的位置信息
        Query query = Query.query(Criteria.where("userId").is(currentUserId));
        UserLocation currentUserLocation = mongoTemplate.findOne(query, UserLocation.class);
        log.info("当前用户信息：{}", currentUserLocation);
        //judge if currentUserLocation is not null
        //判断当前用户信息对象是否为空
        if (Objects.isNull(currentUserLocation)) {
            return null;
        }
        //not null-to set the circle to find people
        //非空，以当前用户所在地为原点，构造圆去搜寻用户
        GeoJsonPoint point = currentUserLocation.getLocation();
        Distance radius = new Distance(Double.valueOf(distance) / 1000, Metrics.KILOMETERS);
        Circle circle = new Circle(point, radius);
        Query locationQuery = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> userLocations = mongoTemplate.find(locationQuery, UserLocation.class);
        //GeoJsonPoint 对象不支持序列化，所以返回附近人的所有id
        return CollUtil.getFieldValues(userLocations, "userId", Long.class);
    }

    /**
     * update location 更新地理位置
     *
     * @param userId
     * @param longitude
     * @param latitude
     * @param address
     * @return
     */
    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //query if location info exit
            //查询用户信息是否存在
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
            if (Objects.isNull(userLocation)) {
                //if location is null, just save
                //如果地理位置对象为空，则保存
                userLocation = new UserLocation();
                userLocation.setCreated(System.currentTimeMillis());
                userLocation.setUpdated(System.currentTimeMillis());
                userLocation.setLastUpdated(System.currentTimeMillis());
                userLocation.setAddress(address);
                userLocation.setUserId(userId);
                userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
                mongoTemplate.save(userLocation);
            } else {
                //            userLocation.setLocation(new GeoJsonPoint(longitude, latitude));
                //            userLocation.setLastUpdated(System.currentTimeMillis());
                //            userLocation.setAddress(address);
                //if not null, query+update+entity.class
                //如果不为空，则更新，上一次更新就是把对象的最近一次更新放到上一次更新
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude)).set("address", address).set("lastUpdated", userLocation.getUpdated()).set("updated", System.currentTimeMillis());
                mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
            log.info("新的定位信息为X经度：{},维度Y:{}", longitude, latitude);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
