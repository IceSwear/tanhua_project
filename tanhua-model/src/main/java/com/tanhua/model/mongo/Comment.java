package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @Description: 评论表
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comment")
public class Comment implements java.io.Serializable {
    /**
     * 评论Id
     */
    private ObjectId id;
    /**
     * 动态id
     */
    private ObjectId publishId;
    /**
     * 评论类型，1-点赞，2-评论，3-喜欢
     */
    private Integer commentType;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论人
     */
    private Long userId;
    /**
     * 被评论人ID
     */
    private Long publishUserId;
    /**
     * 发表时间
     */
    private Long created;
    /**
     * 当前评论的点赞数
     */
    private Integer likeCount = 0;
}