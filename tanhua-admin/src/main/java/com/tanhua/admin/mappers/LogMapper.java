package com.tanhua.admin.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tanhua.model.domain.Log;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogMapper extends BaseMapper<Log> {

    /**
     * 根据操作时间和类型统计日志统计用户数量
     *
     * @param type
     * @param logTime
     * @return
     */
    Integer queryByTypeAndLogTime(@Param("type") String type, @Param("logTime") String logTime);

    /**
     * 根据时间统计用户数量
     *
     * @param logTime
     * @return
     */
    Integer queryByLogTime(String logTime);

    /**
     * 查询次日留存 , 从昨天活跃的用户中查询今日活跃用户
     *
     * @param today
     * @param yesterday
     * @return
     */
    Integer queryNumRetentionId(@Param("today") String today, @Param("yesterday") String yesterday);
}
