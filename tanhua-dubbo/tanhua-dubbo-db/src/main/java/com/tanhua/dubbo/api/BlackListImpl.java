package com.tanhua.dubbo.api;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 黑名单实现类
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@DubboService
@Slf4j
public class BlackListImpl implements BlackListApi {


    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private BlackListMapper blackListMapper;


    /**
     * 删除黑名单 remove black list
     *
     * @param userId
     * @param blackUserId
     */
    @Override
    public void removeBlacklist(Long userId, Long blackUserId) {
//        QueryWrapper queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", userId);
//        queryWrapper.eq("black_user_id", blackUserId);
//        blackListMapper.delete(queryWrapper);
        blackListMapper.removeBlackUserIdOfUserId(userId, blackUserId);
    }


    /**
     * 分页查询黑名单
     *
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findBlackListByUserId(Long userId, Integer page, Integer pagesize) {
        log.info("黑名单分页----userId:{},page:{},pagesize{}", userId, page, pagesize);
        PageHelper.startPage(page, pagesize);
        List<UserInfo> lists = userInfoMapper.getBlackListByUserId(userId);
        PageResult pr = new PageResult();
        if (!Objects.isNull(lists)) {
            PageInfo<UserInfo> userInfoPageInfo = new PageInfo<>(lists);
            long total = userInfoPageInfo.getTotal();
            List<UserInfo> list = userInfoPageInfo.getList();
            pr = new PageResult(page, pagesize, total, list);
        }
        return pr;
    }
}
