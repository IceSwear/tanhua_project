package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.testsoul.OptionCollection;
import com.tanhua.model.mongo.testsoul.QuestionCollection;
import com.tanhua.model.mongo.testsoul.QuestionType;
import com.tanhua.model.mongo.testsoul.Report;
import com.tanhua.model.vo.testsoul.report.Dimension;
import com.tanhua.model.vo.testsoul.report.SimilarYouCollection;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Description: TestSoulApi impl
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@DubboService
public class TestSoulApiImpl implements TestSoulApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<QuestionCollection> getRandomQuestionCollectionList(int count) {
        //创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(QuestionCollection.class, Aggregation.sample(count), Aggregation.match(new Criteria()));
        AggregationResults<QuestionCollection> results = mongoTemplate.aggregate(aggregation, QuestionCollection.class);
        log.info("QuestionCollection---{}", results.getMappedResults());
        return results.getMappedResults();
    }


    @Override
    public List<OptionCollection> getRandomOptionColletionList(int count) {
        //创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(OptionCollection.class, Aggregation.sample(count), Aggregation.match(new Criteria()));
        AggregationResults<OptionCollection> results = mongoTemplate.aggregate(aggregation, OptionCollection.class);
        log.info("OptionCollection---{}", results);

        return results.getMappedResults();
    }

    @Override
    public List<QuestionType> getRandomQuestionTypeList(int count) {
        //创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(QuestionType.class, Aggregation.sample(count), Aggregation.match(new Criteria()));
        AggregationResults<QuestionType> results = mongoTemplate.aggregate(aggregation, QuestionType.class);
        log.info("QuestionType---{}", results);

        return results.getMappedResults();
    }

    @Override
    public Report getReportByReportId(String reportId) {


//        List<QuestionType> questionTypes = mongoTemplate.find(new Query(), QuestionType.class);
//        for (QuestionType questionType : questionTypes) {
//            String reportId1 = questionType.getReportId();
//            Report report = new Report();
//            report.setReportId(reportId1);
//            report.setConclusion("小黑子");
//            report.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/fox.png");
//            mongoTemplate.save(report);
//        }
//
//        for (int i = 0; i < 5; i++) {
//            Dimension dimensio = new Dimension();
//            dimensio.setKey("太美");
//            dimensio.setValue("90%");
//            mongoTemplate.save(dimensio);
//        }
//
//        for (int i = 0; i < 10; i++) {
//            SimilarYouCollection similarYouCollection = new SimilarYouCollection();
//            similarYouCollection.setAvatar("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/tanhua/avatar_2.png");
//            similarYouCollection.setVoId(Integer.valueOf(RandomUtil.randomNumbers(7)));
//            mongoTemplate.save(similarYouCollection);
//        }


        //创建统计对象，设置统计参数
        TypedAggregation aggregation = Aggregation.newAggregation(Report.class, Aggregation.sample(1), Aggregation.match(new Criteria()));
        AggregationResults<Report> results = mongoTemplate.aggregate(aggregation, Report.class);
        log.info("QuestionType---{}", results);
        List<Report> mappedResults = results.getMappedResults();
        if (!CollUtil.isEmpty(mappedResults)) {
            log.info("getReportByReportId-report{}", mappedResults);
            return mappedResults.get(0);
        }
        log.info("Report如果没有，就默认取一个");
        return mongoTemplate.findOne(Query.query(new Criteria()).limit(1), Report.class);
    }

    @Override
    public List<SimilarYouCollection> getSimilarYouList() {
        Query query = new Query().with(Sort.by(Sort.Order.desc("value")));
        return mongoTemplate.find(query, SimilarYouCollection.class);
    }

    @Override
    public List<Dimension> getDimensionList() {
        TypedAggregation aggregation = Aggregation.newAggregation(Dimension.class, Aggregation.sample(10), Aggregation.match(new Criteria()));
        AggregationResults<Dimension> results = mongoTemplate.aggregate(aggregation, Dimension.class);
        log.info("QuestionType---{}", results);
        return results.getMappedResults();
    }
}
