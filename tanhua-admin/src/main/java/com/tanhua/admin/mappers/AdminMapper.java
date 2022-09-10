package com.tanhua.admin.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Admin;
import org.apache.ibatis.annotations.Param;


public interface AdminMapper extends BaseMapper<Admin> {
    Admin findByUserName(@Param(value = "username") String username);

    Admin findById(@Param(value = "id") Long userId);
}
