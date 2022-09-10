package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:  好友时间线表
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movement_timeline")
public class MovementTimeLine implements java.io.Serializable {

    private static final long serialVersionUID = 9096178416317502524L;
    private ObjectId id;
    /**
     * 动态id
     */
    private ObjectId movementId;
    /**
     * 发布动态用户id
     */
    private Long userId;
    /**
     * 可见好友id
     */
    private Long friendId;
    /**
     * 发布的时间
     */
    private Long created;
}

