package com.tanhua.admin.Listener;

import com.tanhua.autoconfig.template.BaiduGreenTemplate;
import com.tanhua.commons.utils.AuditResult;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Description: 接收审计处理
 * @Author: Spike Wong
 * @Date: 2022/9/1
 */
@Component
@Slf4j
public class AuditListener {

    @Autowired
    private BaiduGreenTemplate baiduGreenTemplate;


    @DubboReference
    private MovementApi movementApi;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "tanhua.audit.queue", durable = "true"), exchange = @Exchange(value = "tanhua.audit.exchange", type = ExchangeTypes.TOPIC), key = "audit.movement"))
    public void audit(String movementId)  {
        log.info("开始内容审核:{}", movementId);
        Movement movement = movementApi.findById(movementId);
        log.info("movement:{}", movement);
        if (!Objects.isNull(movement) && movement.getState().equals(0)) {
            String textResult = baiduGreenTemplate.checkText(movement.getTextContent());
            String imageResult = baiduGreenTemplate.checkImage(movement.getMedias());
            int state = 0;
            //状态 0：未审（默认），1：通过，2：驳回
            //过滤不通过的即可
            if (textResult.equals(AuditResult.TEXT_PASS) && imageResult.equals(AuditResult.TEXT_PASS)) {
                state = 1;
            }
            if (textResult.equals(AuditResult.TEXT_BLOCK) || imageResult.equals(AuditResult.TEXT_BLOCK)) {
                state = 2;
            }
            movementApi.update(movementId, state);
        }

    }
}
