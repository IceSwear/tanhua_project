package com.tanhua.server.expcetion;

import com.tanhua.model.vo.ErrorResult;
import lombok.Data;

/**
 * @Description: 自定义业务异常
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@Data
public class BusinessException extends RuntimeException {

    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}
