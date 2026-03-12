package com.xiaowei.demo.common;

import lombok.Data;

/**
 * 自定义业务异常类
 * 用于封装业务逻辑中的异常情况
 */
@Data
public class BusinessException extends RuntimeException {

    /**
     * 异常编码
     */
    private String code;

    /**
     * 构造函数
     *
     * @param code    异常编码
     * @param message 异常信息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     *
     * @param code    异常编码
     * @param message 异常信息
     * @param cause   异常原因
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}