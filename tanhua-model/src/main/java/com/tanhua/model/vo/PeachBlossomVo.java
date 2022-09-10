package com.tanhua.model.vo;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.PeachBlossom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * @Description: 桃花传音VO
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PeachBlossomVo {

    /**
     * 用户id
     */
    private Integer id;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户昵称
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
     * 语音 鸡你太美
     */
    private String soundUrl;


    /**
     * 剩余次数 最大10，最小1
     */
    private Integer remainingTimes;


    /**
     * 初始化PeachblossomVo对象
     *
     * @param userInfo
     * @param peachblossom
     * @return
     */
    public static PeachBlossomVo init(UserInfo userInfo, PeachBlossom peachblossom) {
        PeachBlossomVo vo = new PeachBlossomVo();
        BeanUtils.copyProperties(userInfo,vo);
        vo.setSoundUrl(peachblossom.getSoundUrl());
        return vo;
    }

}
