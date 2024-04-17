package com.jbp.common.yop.utils;

public class HttpException extends RuntimeException {

    public HttpException(Throwable e){
        super(e);
    }

    public HttpException(String msg){
        super(msg);
    }


    public HttpException(String msg, Throwable e){
        super(msg, e);
    }

}
