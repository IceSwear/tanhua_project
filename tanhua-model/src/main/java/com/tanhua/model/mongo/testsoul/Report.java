package com.tanhua.model.mongo.testsoul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 报告表
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "report")
public class Report implements Serializable {

    /**
     * id
     */
    private ObjectId id;


    /**
     * 报告id
     */
    private String reportId;

    /**
     * 结论
     */
    private String conclusion;

    /**
     * 封面
     */
    private String cover;
}
