package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.tanhua.dubbo.api.TestSoulApi;
import com.tanhua.model.mongo.testsoul.OptionCollection;
import com.tanhua.model.mongo.testsoul.QuestionCollection;
import com.tanhua.model.mongo.testsoul.QuestionType;
import com.tanhua.model.mongo.testsoul.Report;
import com.tanhua.model.vo.testsoul.questionlist.Option;
import com.tanhua.model.vo.testsoul.questionlist.Question;
import com.tanhua.model.vo.testsoul.questionlist.QuestionListVo;
import com.tanhua.model.vo.testsoul.report.Dimension;
import com.tanhua.model.vo.testsoul.report.SimilarYou;
import com.tanhua.model.vo.testsoul.report.SimilarYouCollection;
import com.tanhua.model.vo.testsoul.report.TestSoulReportVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: TestSoulService
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@Service
public class TestSoulService {


    @DubboReference
    private TestSoulApi testSoulApi;


    /**
     * 获取问题
     * @return
     */
    public List<QuestionListVo> getQuestionsList() {
        //vo数量
        int voNums = RandomUtil.randomInt(1, 4);
        //问题数量
        List<QuestionListVo> vos = new ArrayList<>();
        //构造while循环 让vos吃饱
        while (vos.size() <= voNums) {

            int QuestionNums = RandomUtil.randomInt(1, 4);
//            List<QuestionCollection> questionCollectionList = new ArrayList<>();
            List<Question> questions = new ArrayList<>();
            while (questions.size() <= QuestionNums) {
                List<QuestionCollection> qcl = testSoulApi.getRandomQuestionCollectionList(QuestionNums);
                log.info("qcl:{},QuestionNums{}",qcl,QuestionNums);
                for (QuestionCollection questionCollection : qcl) {
                    //遍历qcl，构造quesion 且qcl每个元素都要构造 ques
                    int OptionNums = RandomUtil.randomInt(3, 6);
                    List<OptionCollection> ocl = testSoulApi.getRandomOptionColletionList(OptionNums);
                    List<Option> ols = new ArrayList<>();
                    for (OptionCollection optionCollection : ocl) {
                        Option op = Option.init(optionCollection);
                        ols.add(op);
                    }
                    Question question = Question.init(questionCollection, ols);
                    questions.add(question);
                }
            }//while循环结束
            //上面其实主要是为了questions 集合 到时候
            //得到随机这个数量的
            log.info("quesitons:{}",questions);
            List<QuestionType> questionTypes = testSoulApi.getRandomQuestionTypeList(voNums);
            for (QuestionType questionType : questionTypes) {

                QuestionListVo vo = QuestionListVo.init(questionType, questions);
                //vo.setQuestions(questions);
                //vos增加
                vos.add(vo);
            }
        }//while循环结束
        log.info("QuestionListVo:{}", vos);
        return vos;
    }

    /**
     * 获取报告结果
     * @param reportId
     * @return
     */
    public TestSoulReportVo getResult(String reportId) {
        Report report = testSoulApi.getReportByReportId(reportId);
        List<SimilarYouCollection> syc = testSoulApi.getSimilarYouList();
        List<Dimension> dimensions = testSoulApi.getDimensionList();
        if (!Objects.isNull(report) && !CollUtil.isEmpty(syc) && !CollUtil.isEmpty(dimensions)) {
            List<SimilarYou> sy = new ArrayList<>();
            for (SimilarYouCollection similarYouCollection : syc) {
                SimilarYou init = SimilarYou.init(similarYouCollection);
                sy.add(init);
            }
            TestSoulReportVo vo = TestSoulReportVo.init(report, sy, dimensions);
            return vo;
        }
        return null;
    }
}