package com.jbp.common.exception;

public class CrmebUpdateException extends CrmebException{

    private static final long serialVersionUID = 6397082987802748517L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public CrmebUpdateException() {
        this.message = "当前操作人数过多";
    }

    public CrmebUpdateException(String message) {
        super(message);
        this.message = message;
    }



    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
