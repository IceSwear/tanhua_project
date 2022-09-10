package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.PeachBlossom;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * @Description: PeachBlossomApi持久层实现类
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@DubboService
public class PeachBlossomApiImpl implements PeachBlossomApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存桃花传音消息
     *
     * @param userId
     * @param voiceUrl
     */
    @Override
    public void savePeachBlosoomVoice(Long userId, String voiceUrl) {
        PeachBlossom peachBlossom = new PeachBlossom();
        peachBlossom.setCreated(System.currentTimeMillis());
        peachBlossom.setPublishUserId(userId);
        peachBlossom.setSoundUrl(voiceUrl);
        mongoTemplate.save(peachBlossom);
        log.info("保存完毕peachBlossom{}", peachBlossom);
    }


    /**
     * 获取桃花传音对象
     *
     * @param userId
     * @return
     */
    @Override
    public PeachBlossom getRandomVoiceExceptSelf(Long userId) {
        TypedAggregation aggregation = Aggregation.newAggregation(PeachBlossom.class, Aggregation.sample(1), Aggregation.match(Criteria.where("publishUserId").ne(userId)));
        AggregationResults<PeachBlossom> aggregate = mongoTemplate.aggregate(aggregation, PeachBlossom.class);
        List<PeachBlossom> mappedResults = aggregate.getMappedResults();
        if (!CollUtil.isEmpty(mappedResults)) {
            PeachBlossom peachBlossom = mappedResults.get(0);
            log.info("得到的结果:{}", peachBlossom);
            return peachBlossom;
        }
        return null;
    }
}
