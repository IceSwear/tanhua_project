package com.tanhua.dubbo.api;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户基础信息的dubbo实现类
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@DubboService
@Slf4j
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * find by user Id  根据userId查询
     *
     * @param id
     * @return
     */
    @Override
    public UserInfo findById(Long id) {
        //method1 use mp directly to get userInfo
        UserInfo userInfo1 = userInfoMapper.selectById(id);
        // write sql by yourself
        UserInfo userInfo = userInfoMapper.findById(id);
        log.info("UserInfo--mp得到的{},{}", userInfo1, userInfo);
        if (userInfo1.equals(userInfo)) {
            log.info("FinyById--userInfo信息一致");
            return userInfo1;
        } else {
            log.info("userInfo不相等，不返回算了");
            return null;
        }
    }

    /**
     * 更新头像
     *
     * @param url
     * @param userId
     */
    @Override
    public void updateAvatarByUserId(String url, Long userId) {
//        userInfoMapper.updateById(userInfo);
        userInfoMapper.updateAvatarByUserId(url, userId);
    }

    /**
     * 保存userInfo
     * @param userInfo
     */
    @Override
    public void saveUserInfo(UserInfo userInfo) {
//        userInfoMapper.insert(userInfo);
        userInfoMapper.saveUserInfo(userInfo);
    }

    /**
     * 根据条件和用户ids列表找到map映射
     * @param userIds
     * @param conditionInfo
     * @return
     */
    @Override
    public Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo conditionInfo) {
        log.info("findByIds---ids:{} 条件info{}", userIds, conditionInfo);
//        QueryWrapper qw = new QueryWrapper<>();
//        qw.in("id", userIds);
//        if (conditionInfo != null) {
//            log.conditionInfo("查询条件不为空");
//            if (conditionInfo.getAge() != null) {
//                qw.lt("age", conditionInfo.getAge());
//            }
//            if (!StringUtils.isEmpty(conditionInfo.getGender())) {
//                qw.eq("gender", conditionInfo.getGender());
//            }
//            if (!StringUtils.isEmpty(conditionInfo.getNickname())) {
//                qw.eq("nickname", conditionInfo.getNickname());
//            }
//        }
//        List<UserInfo> list = userInfoMapper.selectList(qw);
        List<UserInfo> lists = null;
        Map<Long, UserInfo> map = new HashMap<>();
        if (!CollUtil.isEmpty(userIds)) {
            log.info("userids不为空userIds{}",userIds);
            lists = userInfoMapper.getListByIdsWithCondition(userIds, conditionInfo);
            log.info("符合条件的为:{}", lists);
            map = CollUtil.fieldValueMap(lists, "id");
        }
        return map;
    }


    /**
     * 分页查询 userInfo
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public IPage findAll(Integer page, Integer pagesize) {
        IPage<UserInfo> iPage = new Page<>(page, pagesize);
        IPage<UserInfo> iPage1 = userInfoMapper.selectPage(iPage, null);
        log.info("分页page{}", iPage1);
        return iPage1;
    }


    /**
     * 根据用户id更新个人信息，update userInfo by userId
     *
     * @param userInfo
     * @param userId
     */
    @Override
    public void updateUserInfoById(UserInfo userInfo, Long userId) {
        userInfoMapper.updateUserInfoById(userInfo, userId);
    }
}