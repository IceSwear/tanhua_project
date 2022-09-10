package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 关注 --视频的
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "focus_user")
public class FocusUser implements java.io.Serializable {
    private ObjectId id;
    /**
     * 用户id，自己
     */
    private Long userId;
    /**
     * 喜欢的用户id，对方
     */
    private Long followUserId;
    /**
     * 创建时间
     */
    private Long created;
}
