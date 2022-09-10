package com.tanhua.model.vo;

import cn.hutool.core.util.RandomUtil;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Description: 动态Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementsVo implements Serializable {

    /**
     * 动态id
     */
    private String id;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别 man woman
     */
    private String gender;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 标签
     */
    private String[] tags;


    /**
     * 文字动态
     */
    private String textContent;
    /**
     * 图片动态
     */
    private String[] imageContent;
    /**
     * 距离
     */
    private String distance;
    /**
     * 发布时间 如: 10分钟前
     */
    private String createDate;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 评论数
     */
    private Integer commentCount;
    /**
     * 喜欢数
     */
    private Integer loveCount;


    /**
     * 是否点赞（1是，0否）
     */
    private Integer hasLiked;
    /**
     * 是否喜欢（1是，0否）
     */
    private Integer hasLoved;


    /**
     * 动态Vo初始化
     * @param userInfo
     * @param item
     * @return
     */
    public static MovementsVo init(UserInfo userInfo, Movement item) {
        MovementsVo vo = new MovementsVo();
        //设置动态数据
        BeanUtils.copyProperties(item, vo);
        vo.setId(item.getId().toHexString());
        //设置用户数据
        BeanUtils.copyProperties(userInfo, vo);
        if (!StringUtils.isEmpty(userInfo.getTags())) {
            vo.setTags(userInfo.getTags().split(","));
        }
        //图片列表
        vo.setImageContent(item.getMedias().toArray(new String[]{}));
        //距离,随机距离
        vo.setDistance(RandomUtil.randomInt(100, 100000) + "米");
        //处理格式 ，格式化格式
        Date date = new Date(item.getCreated());
        vo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        //TODO 设置是否点赞(后续处理)缓存处理
        vo.setHasLoved(0);
        vo.setHasLiked(0);
        //设置点赞，喜欢，评论数量
        vo.setLikeCount(item.getLikeCount());
        vo.setLoveCount(item.getLoveCount());
        vo.setCommentCount(item.getCommentCount());
        return vo;
    }
}