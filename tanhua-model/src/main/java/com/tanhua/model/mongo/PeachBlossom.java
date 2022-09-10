package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: Peachblossom 桃花传音表
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "peach_blossom")
public class PeachBlossom implements Serializable {

    /**
     * 表id
     */
    private ObjectId id;

    /**
     * 发布人id
     */
    private Long publishUserId;

    /**
     * 语音 鸡你太美
     */
    private String soundUrl;


    /**
     * 创建时间
     */
    private Long created;

}
