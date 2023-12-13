package com.jbp.common.result;

import com.jbp.common.exception.BusinessExceptionAssert;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 订单响应码
 *
 * @author Han
 * @version 1.0.0
 * @Date 2023/11/14
 */
public enum OrderResultCode implements BusinessExceptionAssert {

    ORDER_NOT_EXIST(5001, "订单不存在"),
    ;

    OrderResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应信息补充
     */
    private Object[] msgParams;


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public OrderResultCode setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        if (ArrayUtil.isNotEmpty(msgParams)) {
            return StrUtil.format(message, msgParams);
        }
        return message;
    }

    @Override
    public OrderResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public OrderResultCode setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
