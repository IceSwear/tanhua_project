package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Settings;
import org.apache.ibatis.annotations.Param;


public interface SettingsMapper extends BaseMapper<Settings> {
    Settings findByUserId(@Param(value = "userId") Long userId);

    void updateSettingsByUserId(@Param(value = "settings") Settings settings, @Param(value = "userId") Long userId);

    void saveSettings(@Param(value = "settings") Settings settings);
}