package com.jbp.common.utils;

import lombok.Getter;

@Getter
public enum SignType {

    MD5("MD5"),
    HMACSHA256("HMACSHA256");

    private String type;

    SignType(String type) {
        this.type = type;
    }

    public boolean isEquals(SignType signType){
       return this.getType().equals(signType.getType());
    }
}
