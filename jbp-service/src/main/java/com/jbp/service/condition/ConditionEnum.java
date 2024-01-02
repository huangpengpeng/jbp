package com.jbp.service.condition;

import lombok.Getter;

/**
 * 升级条件枚举
 * type=0  等级
 * type=1  星级
 *
 */
public enum ConditionEnum {

    等级_直属一阶等级人数(0, "直属一阶等级人数"),

    星级_直属一阶等级人数(1, "直属一阶等级人数");

    @Getter
    private final Integer type;

    @Getter
    private final String name;

    ConditionEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
