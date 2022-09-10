package com.tanhua.model.mongo.testsoul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 选项集合表
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "option_collection")
public class OptionCollection implements Serializable {

    /**
     * id
     */
    private ObjectId id;


//    /**
//     * questionsCollection的主键Id
//     */
//    private ObjectId questionId;
    /**
     * 选项
     */
    private String option;

}
