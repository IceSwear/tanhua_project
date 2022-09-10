package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.testsoul.OptionCollection;
import com.tanhua.model.mongo.testsoul.QuestionCollection;
import com.tanhua.model.mongo.testsoul.QuestionType;
import com.tanhua.model.mongo.testsoul.Report;
import com.tanhua.model.vo.testsoul.report.Dimension;
import com.tanhua.model.vo.testsoul.report.SimilarYouCollection;

import java.util.List;

/**
 * @Description: TestSoulApi
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */

public interface TestSoulApi {
    List<QuestionCollection> getRandomQuestionCollectionList(int count);

    List<OptionCollection> getRandomOptionColletionList(int count);

    List<QuestionType> getRandomQuestionTypeList(int count);

    Report getReportByReportId(String reportId);

    List<SimilarYouCollection> getSimilarYouList();

    List<Dimension> getDimensionList();
}
