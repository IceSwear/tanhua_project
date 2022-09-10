package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 推荐用户表
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "recommend_user")
public class RecommendUser implements java.io.Serializable {
    //import com.sun.corba.se.spi.ior.ObjectId;导包错了
    //import org.bson.types.ObjectId;这个才对
    /**
     * 主键id
     */
    private ObjectId id;
    /**
     * 推荐的用户id
     */
    private Long userId;
    /**
     * 推荐给的用户
     */
    private Long toUserId;
    /**
     * 推荐得分 默认为0
     */
    private Double score = 0d;
    /**
     * 日期  format="yyyy/MM/dd
     */
    private String date;
}