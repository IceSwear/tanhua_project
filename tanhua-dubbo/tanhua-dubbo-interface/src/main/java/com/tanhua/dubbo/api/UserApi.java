package com.tanhua.dubbo.api;

import com.tanhua.model.domain.User;

import java.util.List;

public interface UserApi {

    /**
     * 根据手机号 findy by phone
     *
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    User findByEmail(String email);


    /**
     * save and return new user ID，保存并返回用户新id
     *
     * @param user
     * @return
     */
    Long save(User user);

    void updateMobileById(String phone, Long userId);

    void updateWithHuanXinInfo(User user);

    User findById(Long userId);

    User findByHuanxinId(String huanxinId);

    User getById(Long id);


    /**
     * 查询所有
     * @return
     */
    List<User> findyAll();

}
