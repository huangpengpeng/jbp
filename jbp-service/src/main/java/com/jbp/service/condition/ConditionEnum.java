package com.jbp.service.condition;

import com.beust.jcommander.internal.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 升级条件枚举
 * type=0  等级
 * type=1  星级
 *
 */
public enum ConditionEnum {

    等级_直属一阶等级人数("等级_直属一阶等级人数", "直接邀请人数等级达到后进行升级"),
    星级_小区业绩独立线人数("星级_小区业绩独立线人数", "小区业绩加独立线人数等级满足邀请后升级"),
    ;


    @Getter
    private final String name;

    @Getter
    private final String description;

    ConditionEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static List<ConditionEnum> getXsList() {
        List<ConditionEnum> list = Lists.newArrayList();
        for (ConditionEnum value : ConditionEnum.values()) {
            if (value.getName().startsWith("星级_")) {
                list.add(value);
            }
        }
        return list;
    }

    public static List<ConditionEnum> getList() {
        List<ConditionEnum> list = Lists.newArrayList();
        for (ConditionEnum value : ConditionEnum.values()) {
            if (value.getName().startsWith("等级_")) {
                list.add(value);
            }
        }
        return list;
    }
}
