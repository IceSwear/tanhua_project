package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @Description:
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@DubboService
@Slf4j
public class VideoApiImpl implements VideoApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;


    /**
     * 上传保存视频，返回id
     *
     * @param video
     * @return id，id要自己生成
     */
    @Override
    public String save(Video video) {
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());
        mongoTemplate.save(video);
        log.info("Video:{}", video);
        return video.getId().toHexString();
    }


    /**
     * 根据vids 查询视频
     * @param vids
     * @return
     */
    @Override
    public List<Video> findVideosByVids(List<Long> vids) {
        Query query = Query.query(Criteria.where("vids").in(vids));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        log.info("redis中的存储的推荐视频:{}", videos);
        return videos;
    }


    /**
     * 分页查询
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<Video> queryVideoList(int page, Integer pagesize) {
        Query query = new Query().limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        log.info("mongo推荐视频:{}", videos);
        return videos;
    }

    @Override
    public PageResult queryVideoListByUserId(Long uid, Integer page, Integer pagesize) {
        log.info("uid:{},page:{},pagesize:{}", uid, page, pagesize);
        Query query = Query.query(Criteria.where("userId").is(uid));
        //计算数量
        long count = mongoTemplate.count(query, Video.class);
        query.limit(pagesize).skip((page - 1) * pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        log.info("videos:{}", videos);
        return new PageResult(page,pagesize,count,videos);
    }
}
