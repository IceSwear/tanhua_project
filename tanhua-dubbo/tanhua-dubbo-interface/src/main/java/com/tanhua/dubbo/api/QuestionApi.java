package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Question;

public interface QuestionApi {

    /**
     * 根据用户id查问题
     * @param userId
     * @return
     */
    Question findByUserId(Long userId);

    void updateQuestionById(String content,Long userId);

    void saveQuestion(String content,Long userId);
}