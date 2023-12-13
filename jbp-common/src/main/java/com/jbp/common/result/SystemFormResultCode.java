package com.jbp.common.result;

import com.jbp.common.exception.BusinessExceptionAssert;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @ClassName SystemFormResultCode
 * @Description 系统自定义表单响应码枚举
 * @Author HZW
 * @Date 2023/3/4 11:12
 * @Version 1.0
 */
public enum SystemFormResultCode implements BusinessExceptionAssert {

    FORM_TEMP_NOT_EXIST(7101, "表单不存在"),
    FORM_TEMP_PARAMETER_ERROR(7102, "模板表单 【{}】 的内容不是正确的JSON格式！"),
    FORM_TEMP_NAME_REPEAT(7103, "模板表单名称重复"),
    ;

    SystemFormResultCode(Integer code, String message) {
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
    public SystemFormResultCode setCode(Integer code) {
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
    public SystemFormResultCode setMessage(String message) {
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
