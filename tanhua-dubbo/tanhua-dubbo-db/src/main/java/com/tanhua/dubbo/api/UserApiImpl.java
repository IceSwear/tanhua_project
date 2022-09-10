package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * @Description: user dubbo service
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */

@DubboService
@Slf4j
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据手机号码查询用户
     *
     * @param mobile
     * @return
     */
    public User findByMobile(String mobile) {
        //mp查询办法
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("mobile", mobile);
        User user = userMapper.selectOne(qw);
        //alternative method user mybatis -handle sql
        User user1 = userMapper.findByMobile(mobile);
        log.info("查询结果不相等,mp结果:{},手写sql结果:{}", user, user1);
        if (!Objects.isNull(user) && user.equals(user1)) {
            log.info("findByMobile=user两种查询结果相等");
            return user;
        } else {
            log.info("查询结果不相等，故意返回空");
            return null;
        }
    }

    /**
     * 根据邮箱找
     * @param email
     * @return
     */
    public User findByEmail(String email) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("email", email);
        return userMapper.selectOne(qw);
    }


    /**
     * save and return new user ID，保存并返回用户新id
     *
     * @param user
     * @return
     */
    @Override
    public Long save(User user) {
//        userMapper.insert(user); 这时候mybatisplus胜出
        userMapper.addNewUser(user);
        User byMobile = userMapper.findByMobile(user.getMobile());
        return byMobile.getId();
    }


    /**
     * 根据Id修改手机号
     *
     * @param phone
     * @param userId
     */
    @Override
    public void updateMobileById(String phone, Long userId) {
        // mybatis-plus 方法
//        User user = userMapper.selectById(userId);
//        user.setMobile(phone);
//        userMapper.updateById(user);

        //手写sql方法
        userMapper.updateMobileById(phone, userId);


    }

    @Override
    public void updateWithHuanXinInfo(User user) {
//        userMapper.updateById(user);
        userMapper.updateWithHuanXinInfo(user);
    }


    /**
     * 根据id查找对象
     * @param userId
     * @return
     */
    @Override
    public User findById(Long userId) {
        return userMapper.findByUserId(userId);
    }

    /**
     * query by huanxin Id 根据环信id查询mysql的user用户
     *
     * @param huanxinId
     * @return
     */
    @Override
    public User findByHuanxinId(String huanxinId) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("hx_user", huanxinId);
        User user = userMapper.selectOne(qw);
        User user1 =userMapper.findByHuanxinId(huanxinId);
        if (!Objects.isNull(user)&& user.equals(user1)){
            log.info("两种方法查到的数据一样");
            return user;
        }
        return null;
    }


    /**
     * 根据Id找到人
     *
     * @param id
     * @return
     */
    @Override
    public User getById(Long id) {
        return userMapper.getById(id);
    }


    /**
     * 查询所有
     *
     * @return
     */
    @Override
    public List<User> findyAll() {
        return userMapper.findAll();
    }
}