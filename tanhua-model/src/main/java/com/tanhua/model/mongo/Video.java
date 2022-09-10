package com.tanhua.model.mongo;

import com.tanhua.model.enums.CommentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @Description: 视频表
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video")
public class Video implements java.io.Serializable {

    private static final long serialVersionUID = -3136732836884933873L;

    /**
     * 主键 id
     */
    private ObjectId id;

    /**
     * 自动增长
     */
    private Long vid;

    /**
     * 创建时间
     */
    private Long created;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文字
     */
    private String text;

    /**
     * 视频封面文件，URL
     */
    private String picUrl;

    /**
     * 视频文件，URL
     */
    private String videoUrl;


    /**
     * 点赞数
     */
    private Integer likeCount = 0;

    /**
     * 评论数
     */
    private Integer commentCount = 0;

    /**
     * 喜欢数
     */
    private Integer loveCount = 0;

    /**
     * 得到统计数
     *
     * @param commentType
     * @return
     */
    public Integer statisCount(Integer commentType) {
        if (commentType.equals(CommentType.LIKE.getType())) {
            return this.likeCount;
        } else if (commentType.equals(CommentType.COMMENT.getType())) {
            return this.commentCount;
        } else {
            return loveCount;
        }
    }
}