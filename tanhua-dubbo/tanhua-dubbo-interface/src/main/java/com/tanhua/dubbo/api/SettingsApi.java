package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Settings;

public interface SettingsApi {


    /**
     * find by user id -setting  根据用户id查询设置
     * @param userId
     * @return
     */
    Settings findByUserId(Long userId);


    /**
     * update settings by user Id
     * @param settings
     */
    void updateSettings(Settings settings,Long userId);

    void saveSettings(Settings settings);
}