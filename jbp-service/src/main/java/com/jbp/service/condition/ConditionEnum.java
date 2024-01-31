package com.jbp.service.condition;

import lombok.Getter;

/**
 * 升级条件枚举
 * type=0  等级
 * type=1  星级
 *
 */
public enum ConditionEnum {

    等级_直属一阶等级人数("直属一阶等级人数", "邀请一阶人数升级"),

    ;


    @Getter
    private final String name;

    @Getter
    private final String description;

    ConditionEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
