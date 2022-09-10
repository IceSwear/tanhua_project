package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.QuestionMapper;
import com.tanhua.model.domain.Question;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Description: questions实现类 陌生人问题实现类
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@DubboService
@Slf4j
public class QuestionImpl implements QuestionApi {

    @Resource
    QuestionMapper questionMapper;


    /**
     * 根据userId查找问题
     * @param userId
     * @return
     */
    @Override
    public Question findByUserId(Long userId) {
        //此时是user_id 不是主键的id 要弄清楚
        //at present, here we query user_id rather the primary key id,please make it clear
        //method 1  mybatis-plus 用MP方法
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Question question = questionMapper.selectOne(queryWrapper);
        //method 2 query by my own sql 用自己的sql
        Question question1 = questionMapper.findByUserId(userId);
        if (!Objects.isNull(question) && question.equals(question1)) {
            return question;
        } else {
            log.info("既然不相等，很生气！那我就return null");
            return null;
        }
    }

    @Override
    public void updateQuestionById(String content,Long userId) {
        questionMapper.updateTxtById(content,userId);
    }

    @Override
    public void saveQuestion(String content,Long userId) {
        questionMapper.saveQuestion(content,userId);
    }
}
