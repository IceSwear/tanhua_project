package com.tanhua.model.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 基础实现类，让其他实体类继承时间属性
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
public abstract class BasePojo implements Serializable {

    /**
    创建（插入时候）时自动填充
    fill when isert
     */
    @TableField(fill = FieldFill.INSERT)
    private Date created;

    /**
    创建（插入时候）和更新时，自动填充
    fill when insert and update
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;

}