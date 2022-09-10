package com.tanhua.dubbo.utils;

import com.tanhua.model.mongo.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;


/**
 * @Description: Id查询+自增
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Component
public class IdWorker {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Long getNextId(String collName) {
        //query collName is collName
        //update
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update();
        //+-对应collName这个文件的seqId自动+1,inc即increase
        update.inc("seqId", 1);
        //查找并修改的选项
        FindAndModifyOptions options = new FindAndModifyOptions();
        //如果不存在 保存一条数据
        options.upsert(true);
        //每次返回最新的数值
        options.returnNew(true);
        //将查询条件，如何更新，查找修改后的操作 以及class集合传入操作,得到最新的数据
        //满足原子型，不会发生线程冲突
        Sequence sequence = mongoTemplate.findAndModify(query, update, options, Sequence.class);
        return sequence.getSeqId();
    }
}