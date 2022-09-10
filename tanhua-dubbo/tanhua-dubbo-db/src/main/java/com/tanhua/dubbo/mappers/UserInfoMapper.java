package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户基础信息Mapper接口
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {


    UserInfo findById(@Param(value = "id") Long id);

    void updateAvatarByUserId(@Param(value = "url") String url, @Param(value = "id") Long userId);

    void updateUserInfoById(@Param(value = "userInfo") UserInfo userInfo, @Param("id") Long userId);

    void saveUserInfo(@Param(value = "userInfo") UserInfo userInfo);

    List<UserInfo> getBlackListByUserId(@Param(value = "userId") Long userId);

    List<UserInfo> getListByIdsWithCondition(@Param(value = "ids") List<Long> userIds, @Param(value = "condition") UserInfo conditionInfo);
}