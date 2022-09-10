package com.tanhua.model.mongo.testsoul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 问卷集合
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "question_type")
public class QuestionType implements Serializable {
    /**
     * 问卷编号
     */
    private ObjectId id;

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
     * 是否锁住（0解锁，1锁住） defu
     */
    private Integer isLock=0;

    /**
     * 最新报告id
     */
    private String reportId;

//    /**
//     * 问题表id
//     */
//    private String questionId;


}
