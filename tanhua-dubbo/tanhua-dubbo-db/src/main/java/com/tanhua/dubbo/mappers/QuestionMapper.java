package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Question;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
public interface QuestionMapper extends BaseMapper<Question> {


    /**
     * 根据用户Id查问题对象  ,find by user id
     *
     * @param userId
     * @return
     */
    Question findByUserId(@Param(value = "userId") Long userId);

    void updateTxtById(@Param(value = "content") String content,@Param(value = "userId") Long userId);

    void saveQuestion(@Param(value = "content") String content, @Param(value = "userId") Long userId);
}