package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @Description:  短视频数据层接口
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
public interface VideoApi {
    /**
     * 上传保存视频，返回id
     * @param video
     * @return  id
     */
    String save(Video video);

    /**
     * 根据vids 查询视频
     * @param vids
     * @return
     */
    List<Video> findVideosByVids(List<Long> vids);


    /**
     * 分页查询
     * @param page
     * @param pagesize
     * @return
     */
    List<Video> queryVideoList(int page, Integer pagesize);

    PageResult queryVideoListByUserId(Long uid, Integer page, Integer pagesize);
}
