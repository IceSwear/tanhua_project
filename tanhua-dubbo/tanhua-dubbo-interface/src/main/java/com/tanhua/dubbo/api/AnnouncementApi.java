package com.tanhua.dubbo.api;

import com.tanhua.model.vo.PageResult;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/24
 */
public interface AnnouncementApi {
   PageResult listAccountments(Integer page, Integer pagesize);
}
