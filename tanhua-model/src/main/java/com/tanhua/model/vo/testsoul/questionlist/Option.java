package com.tanhua.model.vo.testsoul.questionlist;

import com.tanhua.model.mongo.testsoul.OptionCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * @Description: 选项Vo
 * @Author: Spike Wong
 * @Date: 2022/8/26
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {

    /**
     * id
     */
    public String id;

    /**
     * 选项
     */
    public String option;


    /**
     * 初始化
     *
     * @param optionCollection
     * @return
     */
    public static Option init(OptionCollection optionCollection) {
        Option option = new Option();
        if (!Objects.isNull(optionCollection)) {
            option.setOption(optionCollection.getOption());
            option.setId(optionCollection.getId().toHexString());
        }
        return option;
    }
}