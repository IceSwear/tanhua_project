package com.tanhua.server.service;

import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.model.domain.Question;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@Service
public class QuestionService {

    @DubboReference
    QuestionApi questionApi;


    /**
     * 保存信息
     * @param content
     */
    public void saveQuestion(String content) {
        Long currentUserId = UserHolder.getUserId();
        Question question = questionApi.findByUserId(currentUserId);
        //String txt = question.getTxt();
        if (!Objects.isNull(question)) {
        //question.setTxt(content);
            questionApi.updateQuestionById(content,currentUserId);
        } else {
            question = new Question();
            //noted that it is userID
            question.setUserId(currentUserId);
            question.setTxt(content);
            questionApi.saveQuestion(content,currentUserId);
        }

    }
}
