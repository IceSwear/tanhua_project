package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @Description: 访客表
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "visitors")
public class Visitors implements java.io.Serializable {

    private static final long serialVersionUID = 2811682148052386573L;

    /**
     * id
     */
    private ObjectId id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 来访用户id
     */
    private Long visitorUserId;

    /**
     * 来源，如首页. 圈子等
     */
    private String from;

    /**
     * 来访时间
     */
    private Long date;
    /**
     * 来访日期
     */
    private String visitDate;
    /**
     * 得分
     */
    private Double score;
}