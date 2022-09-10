package com.tanhua.dubbo.mappers;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {


    /**
     * 根据Id找到人
     *
     * @param id 用户id user Id
     * @return
     */
    User getById(@Param("id") Long id);

    User findByMobile(@Param("mobile") String mobile);

    User findByEmail(@Param("email") String email);

    void addNewUser(@Param("user") User user);

    void updateWithHuanXinInfo(@Param("user") User user);

    void updateMobileById(@Param(value = "phone") String phone, @Param(value = "userId") Long userId);

    List<User> findAll();

    User findByUserId(@Param(value = "id") Long userId);

    User findByHuanxinId(@Param(value = "huanxinId") String huanxinId);
}
