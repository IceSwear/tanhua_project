package com.tanhua.admin.Listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.admin.mappers.LogMapper;
import com.tanhua.model.domain.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description: 日志监听接收   log listener
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */

@Component
@Slf4j
public class LogListener {

    @Resource
    private LogMapper logMapper;


    /**
     * 消息接听接收
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "tanhua.log.queue", durable = "true"), exchange = @Exchange(value = "tanhua.log.exchange", type = ExchangeTypes.TOPIC), key = "log.*"))
    public void log(String message) {
        try {
            Map map = JSON.parseObject(message);
            log.info("接收到的信息{}", map);
            Long userId = Long.valueOf(map.get("userId").toString());
            String type = (String) map.get("type");
            String logTime = (String) map.get("logTime");
            Log log = new Log(userId, logTime, type);
            logMapper.insert(log);
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
