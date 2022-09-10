package com.tanhua.model.vo.testsoul.questionlist;

import com.tanhua.model.mongo.testsoul.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 问题列表Vo
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionListVo {
    /**
     * 问卷编号
     */
    private String id;

    /**
     * 问卷名称
     */
    private String name;

    /**
     * 问卷封面
     */
    private String cover;

    /**
     * 问卷级别
     */
    private String level;

    /**
     * 星级数
     */
    private Integer star;

    /**
     * 试题（非必须）
     */
    private List<Question> questions;

    /**
     * 是否锁住（0解锁，1锁住）
     */
    private Integer isLock;

    /**
     * 最新报告id
     */
    private String reportId;


    /**
     * 构建vo对象
     *
     * @param questionType
     * @param questions
     * @return
     */
    public static QuestionListVo init(QuestionType questionType, List<Question> questions) {
        QuestionListVo vo = new QuestionListVo();
        if (!Objects.isNull(questionType)) {
            BeanUtils.copyProperties(questionType, vo);
            vo.setReportId(null);
            vo.setId(questionType.getId().toHexString());
        }
        vo.setQuestions(questions);
        return vo;
    }
}
