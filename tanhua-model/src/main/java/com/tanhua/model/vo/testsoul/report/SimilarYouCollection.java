package com.tanhua.model.vo.testsoul.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 相似人 表
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "similar_you")
public class SimilarYouCollection implements Serializable {
    /**
     * id
     */
    private ObjectId id;

    /**
     * id
     */
    private Integer voId;

    /**
     * 头像
     */
    private String avatar;
}
