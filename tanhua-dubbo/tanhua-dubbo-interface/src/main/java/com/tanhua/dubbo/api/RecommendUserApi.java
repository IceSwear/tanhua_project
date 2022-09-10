package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.Collection;
import java.util.List;

public interface RecommendUserApi <T>{

    /**
     * query max score 查询推荐用户最高分的数据
     * @param toUserId
     * @return
     */
    public RecommendUser queryWithMaxScore(Long toUserId);


    /**
     * 获取推荐用户列表
     * @param page
     * @param pagesize
     * @param toUserId
     * @return
     */
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);


    /**
     * 查看佳人信息
     *
     * @param userId
     * @param toUserId
     * @return
     */
    RecommendUser queryByUserId(Long userId, Long toUserId);


    /**
     * 探花卡片
     *
     * @param currentUserId
     * @param counts
     * @return
     */
    List<RecommendUser> queryCardsList(Long currentUserId, int counts);




    /**
     * 删除所有
     */
    void removeAll();

    /**
     * 批量存推荐用户
     * @param set
     */
//    void addBath(Set<RecommendUser> set);

    /**
     * 批量存推荐用户
     *
     * @param list
     */
//    void addBath(List<RecommendUser> list);


    void addBath(Collection<? extends T> batchToSave, Class<?> entityClass);
}