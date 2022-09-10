package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 排序表
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Document(collection = "sequence")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sequence {

    /**
     * id
     */
    private ObjectId id;

    /**
     * 自增序列
     */
    private long seqId;

    /**
     * 集合名称
     */
    private String collName;
}