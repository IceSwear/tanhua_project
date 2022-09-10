package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

/**
 * @Description: movement  mongo数据库层的接口
 * @Author: Spike Wong
 * @Date: 2022/8/16
 */
public interface MovementApi {

    /**
     * 保存动态 save movement
     * @param movement
     * @return
     */
    String saveMovement(Movement movement);


    /**
     * 根据用户id查询
     * @param userId
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findByUserId(Long userId,Integer state, Integer page, Integer pagesize);


    /**
     * 根据id查找动态
     * @param id
     * @return
     */
    Movement findById(String id);


    /**
     * 查询好友动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    PageResult findFriedMovementsList(Integer page, Integer pagesize, Long userId,List ids);

    List<Movement> randomMovements(Integer counts,Long currentUserId);

    List<Movement> findMovementsByPids(List<Long> pids);

    PageResult findByPublishUserIdAndCommentType(Long userId, CommentType like, Integer page, Integer pagesize);

    List<Movement> getMovements(Integer page, Integer pagesize, Long uid, Integer state);

    void update(String movementId, int state);

    void updateBatch(List<String> ids, int state);
}
