package com.tanhua.model.vo;


import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @Description: 今日佳人
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayBest {
    /**
     * user id
     */
    private Long id;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 性别，man/woman
     */
    private String gender;
    /**
     * 年龄
     */
    private Integer age;
    /*
    tags 标签
     */
    private String[] tags;
    /*
    缘分值
     */
    private Long fateValue;

    /**
     * 在vo对象中，补充一个工具方法，封装转化过程
     * @param userInfo
     * @param recommendUser
     * @return
     */
    public static TodayBest init(UserInfo userInfo, RecommendUser recommendUser) {
        TodayBest vo = new TodayBest();
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getTags() != null) {
            vo.setTags(userInfo.getTags().split(","));
        }
        //get score from recommend user 从佳人中得到分数
        vo.setFateValue(recommendUser.getScore().longValue());
        return vo;
    }
}