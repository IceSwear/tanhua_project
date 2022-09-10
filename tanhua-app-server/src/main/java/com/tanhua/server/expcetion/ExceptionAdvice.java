package com.tanhua.server.expcetion;

import com.tanhua.model.vo.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @Description: 处理业务异常??
 * @Author: Spike Wong
 * @Date: 2022/8/9
 */
@ControllerAdvice
public class ExceptionAdvice {

    /**
     *
     * @param be
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerException(BusinessException be) {
        //打印堆栈信息
        be.printStackTrace();
        ErrorResult errorResult = be.getErrorResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    /**
     * 处理不可预知的异常
     * @param be
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity handlerException1(Exception be) {
        //打印堆栈信息
        be.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }
}
