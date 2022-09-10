package com.tanhua.admin.exception;

/**
 * @Description:  自定义异常
 * @Author: Spike Wong
 * @Date: 2022/8/31
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
	
}