package com.tanhua.model.vo.testsoul.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Description: 像你
 * @Author: Spike Wong
 * @Date: 2022/9/6
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarYou implements Serializable {

    /**
     * id
     */
    private Integer Id;

    /**
     * 头像
     */
    private String avatar;


    /**
     * 相似初始化
     * @param similarYouCollection
     * @return
     */
    public static SimilarYou init(SimilarYouCollection similarYouCollection) {
        SimilarYou similarYou = new SimilarYou();
        if (!Objects.isNull(similarYouCollection)) {
            similarYou.setAvatar(similarYouCollection.getAvatar());
            similarYou.setId(similarYouCollection.getVoId());
        }
        return similarYou;


    }
}