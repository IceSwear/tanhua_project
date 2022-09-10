package com.tanhua.model.vo.testsoul.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @Description: 问卷列表 测量
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dimension")
public class Dimension implements Serializable {

    /**
     * 关键词
     */
    private String key;
    /**
     * 分数
     */
    private String value;
}