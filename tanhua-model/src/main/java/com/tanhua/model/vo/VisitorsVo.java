package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Visitors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Objects;


/**
 * @Description: 访客Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorsVo {

    /**
     * 用户id
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
     * 缘分值
     */
    private Long fateValue;


    /**
     * 在vo对象中，补充一个工具方法，封装转化过程
     *
     * @param userInfo
     * @param visitors
     * @return
     */
    public static VisitorsVo init(UserInfo userInfo, Visitors visitors) {
        VisitorsVo vo = new VisitorsVo();
        BeanUtils.copyProperties(userInfo, vo);
        if (!Objects.isNull(userInfo.getTags())) {
            vo.setTags(userInfo.getTags().split(","));
        }
        vo.setFateValue(visitors.getScore().longValue());
        return vo;
    }
}