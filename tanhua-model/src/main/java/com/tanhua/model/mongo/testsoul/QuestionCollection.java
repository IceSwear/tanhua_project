package com.tanhua.model.mongo.testsoul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 问题vo
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "question_collection")
public class QuestionCollection  implements Serializable {

    /**
     * id
     */
    private ObjectId id;


    /**
     * 问题
     */
    private String question;

}
