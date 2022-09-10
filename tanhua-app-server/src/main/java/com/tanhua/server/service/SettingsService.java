package com.tanhua.server.service;

import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @Description:  设置服务
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */

@Service
@Slf4j
public class SettingsService {


    @DubboReference
    private QuestionApi questionApi;
    @DubboReference
    private SettingsApi settingsApi;
    @DubboReference
    private BlackListApi blackListApi;


    /**
     * 返回设置VO
     *
     * @return
     */
    public SettingsVo settings() {
        //new setting vo and ready to return after define
        SettingsVo vo = new SettingsVo();
        //get user id
        Long userId = UserHolder.getUserId();
        //get mobile
        String moblie = UserHolder.getMoblie();
        //set the info
        vo.setId(userId);
        vo.setPhone(moblie);
        Question question = questionApi.findByUserId(userId);
        log.info("Question:{}", question);
        String txt = question == null ? "你喜欢Java吗????" : question.getTxt();
        vo.setStrangerQuestion(txt);
        Settings settings = settingsApi.findByUserId(userId);
        if (!Objects.isNull(settings)) {
            vo.setGonggaoNotification(settings.getGonggaoNotification());
            vo.setLikeNotification(settings.getLikeNotification());
            vo.setPinglunNotification(settings.getPinglunNotification());
        }
        //otherwise default notifications are all true!!
        return vo;
    }


    /**
     * 保存设置  save settings
     *
     * @param map map里包含3个设置
     */
    public void saveSettings(Map map) {
        log.info("更新设置settings----map:{}", map);
        Boolean likeNotification = (Boolean) map.get("likeNotification");
        Boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        Boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");
        Long currentUserId = UserHolder.getUserId();
        Settings settings = settingsApi.findByUserId(currentUserId);
        log.info("查出的settings:{}", settings);
        if (!Objects.isNull(settings)) {
            log.info("不为空，更新设置");
            //exit,updated  directly,存在，更新即可
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settingsApi.updateSettings(settings, currentUserId);
        } else {
            // Null, save new ,不存在，保存
            log.info("第一次，保存设置");
            settings = new Settings();
            settings.setUserId(currentUserId);
            settings.setGonggaoNotification(gonggaoNotification);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settingsApi.saveSettings(settings);
        }
    }


    /**
     * 得到黑名单列表分页查询   get blacklist per page and pageize
     *
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult getBlackList(Integer page, Integer pagesize) {
        //get currnet user id
        Long currentUserId = UserHolder.getUserId();
//      mybatis-plus 方法
//        IPage<UserInfo> iPage = blackListApi.findByUserId(currentUserId, page, pagesize);
//        List<UserInfo> records = iPage.getRecords();
//        long total = iPage.getTotal();
        return blackListApi.findBlackListByUserId(currentUserId, page, pagesize);
    }


    /**
     * 删除黑名单，remove black list 黑名单改造完毕
     * @param blackUserId
     */
    public void removeBlacklist(Long blackUserId) {
        Long userId = UserHolder.getUserId();
        blackListApi.removeBlacklist(userId, blackUserId);
    }
}
