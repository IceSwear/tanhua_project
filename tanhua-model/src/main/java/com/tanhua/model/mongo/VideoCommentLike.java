package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 视频点赞记录表
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video_comment_like")
public class VideoCommentLike implements java.io.Serializable {

    /**
     * id
     */
    private ObjectId id;

    /**
     * 动态id
     */
    private ObjectId likeCommentId;

    /**
     * 评论类型，1-点赞，2-评论，3-喜欢
     */
    private Long userId;

    /**
     * 发表时间
     */
    private Long created;
}
