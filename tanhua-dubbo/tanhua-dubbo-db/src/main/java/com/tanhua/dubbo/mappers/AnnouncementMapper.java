package com.tanhua.dubbo.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Announcement;

import java.util.List;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    List<Announcement> getAllNotDeleted();
}
