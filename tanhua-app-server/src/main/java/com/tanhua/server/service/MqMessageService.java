package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: mq消息服务
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
@Service
@Slf4j
public class MqMessageService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * @param userId     用户Id
     * @param actionType 动作类型
     * @param key        key   video or mvm or user
     * @param busId      busID是啥--业务Id，动态就是动态ID，视频就是视频ID
     */
    public void sendLogService(Long userId, String actionType, String key, String busId) {
        try {
            Map map = new HashMap<>();
            map.put("userId", userId.toString());
            //行为类型
            map.put("type", actionType);
            map.put("logTime", new DateTime().toString("yyyy-MM-dd"));
            //busID是啥--业务Id，动态就是动态ID，视频就是视频ID
            map.put("busId", busId);
            log.info("存入队列的map:{}", map);
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend("tanhua.log.exchange", "log." + key, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 动态 aduits审计
     * @param movementId
     */
    public void sendAuditService(String movementId) {
        try {
            amqpTemplate.convertAndSend("tanhua.audit.exchange", "audit.movement", movementId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}
