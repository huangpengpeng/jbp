package com.jbp.service.condition;

import com.beust.jcommander.internal.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 升级条件枚举
 */
public enum ConditionEnum {

    一阶等级人数("一阶等级人数", "直接邀请人数等级达到后进行升级"),
    小区业绩独立线星级人数("小区业绩独立线星级人数", "小区业绩加独立线人数星级满足邀请后升级"),
    ;

    @Getter
    private final String name;

    @Getter
    private final String description;

    ConditionEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }



    public static List<ConditionEnum> getCapaList() {
        List<ConditionEnum> list = Lists.newArrayList();
        for (ConditionEnum value : ConditionEnum.values()) {
            list.add(value);
        }
        return list;
    }
}
