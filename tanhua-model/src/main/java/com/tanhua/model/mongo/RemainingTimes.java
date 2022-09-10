package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 剩余次数表
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "remaining_times")
public class RemainingTimes implements Serializable {

    /**
     * id
     */
    private ObjectId id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 剩余次数 默认10次
     */
    private Integer remainingTimes = 10;
}
