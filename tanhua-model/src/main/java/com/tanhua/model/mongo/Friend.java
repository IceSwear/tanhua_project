package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 好友关系表
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "friend")
public class Friend implements java.io.Serializable {

    private static final long serialVersionUID = 6003135946820874230L;
    private ObjectId id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 好友id
     */
    private Long friendId;
    /**
     * 时间
     */
    private Long created;
}