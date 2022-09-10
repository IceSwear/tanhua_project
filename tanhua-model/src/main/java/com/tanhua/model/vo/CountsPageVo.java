package com.tanhua.model.vo;

import cn.hutool.core.util.RandomUtil;
import com.tanhua.model.domain.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * @Description: 统计分页详细列表
 * @Author: Spike Wong
 * @Date: 2022/9/7
 */
@Slf4j
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CountsPageVo {

    /**
     * id x
     */
    private Integer id;
    /**
     * 头像 1
     */
    private String avatar;
    /**
     * 昵称 1
     */
    private String nickname;
    /**
     * 性别 1
     */
    private String gender;
    /**
     * 年龄 1
     */
    private Integer age;
    /**
     * 城市 1
     */
    private String city;

    /**
     * 教育 1
     */
    private String education;

    /**
     * 婚姻状态（0未婚，1已婚） 1
     */
    private Integer marriage;

    /**
     * 匹配度
     */
    private Integer matchRate;

    /**
     * 是否喜欢ta
     */
    private Boolean alreadyLove = false;


    /**
     * 初始化CountspageVo对象
     *
     * @param userInfo
     * @return
     */
    public static CountsPageVo init(UserInfo userInfo) {
        CountsPageVo vo = new CountsPageVo();
        if (!Objects.isNull(userInfo)) {
            BeanUtils.copyProperties(userInfo, vo);
            vo.setId(Integer.valueOf(userInfo.getId().toString()));
            vo.setMatchRate(RandomUtil.randomInt(66, 99));
            //TODO noted！还要去redis中查找是否喜欢
        }
        return vo;
    }

}
