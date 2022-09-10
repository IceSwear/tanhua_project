package com.tanhua.model.vo.testsoul.questionlist;

import com.tanhua.model.mongo.testsoul.QuestionCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;


/**
 * @Description: 问题
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    /**
     * 问卷id
     */
    public String id;

    /**
     * 问题
     */
    public String question;

    /**
     * 选项集合
     */
    public List<Option> options;


    /**
     *  问题初始化
     * @param questionCollection
     * @param options
     * @return
     */
    public static Question init(QuestionCollection questionCollection, List<Option> options) {
        Question question = new Question();
        if (!Objects.isNull(questionCollection)) {
            question.setQuestion(questionCollection.getQuestion());
            question.setId(questionCollection.getId().toHexString());
        }
        if (!Objects.isNull(options)) {
            question.setOptions(options);
        }
        return question;
    }
}