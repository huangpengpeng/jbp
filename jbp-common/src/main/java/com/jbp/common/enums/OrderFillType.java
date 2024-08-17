package com.jbp.common.enums;


public enum OrderFillType {

    待补单("待补单"),
    已拒补("已拒补"),
    已补单( "已补单");


    private final String name;

    OrderFillType( String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

}
