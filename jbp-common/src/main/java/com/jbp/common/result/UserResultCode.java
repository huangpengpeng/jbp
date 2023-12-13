package com.jbp.common.result;

import com.jbp.common.exception.BusinessExceptionAssert;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @ClassName UserResultCode
 * @Description 用户模块响应状态码
 * @Author HZW
 * @Date 2023/3/7 12:20
 * @Version 1.0
 */
public enum UserResultCode implements BusinessExceptionAssert {

    USER_EXIST(8101, "用户已存在"),
    USER_NOT_EXIST(8102, "用户不存在"),
    USER_ID_NULL(8103, "请选择用户"),
    USER_STATUS_EXCEPTION(8104, "用户状态异常"),
    ;

    UserResultCode(Integer code, String message) {
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
    public UserResultCode setCode(Integer code) {
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
    public UserResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public IResultEnum setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
