package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domain.Settings;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

/**
 * @Description: 设置实现类 setting service impl
 * @Author: Spike Wong
 * @Date: 2022/8/10
 */
@DubboService
@Slf4j
public class SettingsImpl implements SettingsApi {

    @Autowired
    SettingsMapper settingsMapper;


    /**
     * 根据userid 查设置  -find by user id
     *
     * @param userId
     * @return
     */
    @Override
    public Settings findByUserId(Long userId) {
        QueryWrapper<Settings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        // mybatis
        Settings settings = settingsMapper.selectOne(queryWrapper);
        // my own sql
        Settings settings1 = settingsMapper.findByUserId(userId);
        if (!Objects.isNull(settings) && settings.equals(settings1)) {
            log.info("相等，返回:{}", settings);
            return settings;
        } else {
            log.info("不相等，那我就返回null");
            return null;
        }
    }


    /**
     * update settings by userId  根据userId更新设置
     *
     * @param settings
     * @param userId
     */
    @Override
    public void updateSettings(Settings settings, Long userId) {
        log.info("完成手写sql，updateSettings:{},id:{}", settings, userId);
        //settingsMapper.updateById(settings); -mybatis-plus
        settingsMapper.updateSettingsByUserId(settings, userId);
    }


    /**
     * 保存设置 save setting
     *
     * @param settings
     */
    @Override
    public void saveSettings(Settings settings) {
        log.info("完成手写sql，saveSettings---settings:{}", settings);
//        settingsMapper.insert(settings);
        settingsMapper.saveSettings(settings);
    }
}
