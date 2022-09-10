package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 动态评论点赞
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "comment_like")
public class CommentLike implements java.io.Serializable {

    private ObjectId id;
    /**
     * 点赞的评论Id
     */
    private ObjectId likeCommentId;
    /**
     * 点赞用户Id
     */
    private Long userId;
    /**
     * 点赞时间
     */
    private Long created;
}
