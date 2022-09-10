package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.BlackList;
import org.apache.ibatis.annotations.Param;

public interface BlackListMapper extends BaseMapper<BlackList> {


    void removeBlackUserIdOfUserId(@Param(value = "userId") Long userId, @Param(value = "blackUserId") Long blackUserId);
}