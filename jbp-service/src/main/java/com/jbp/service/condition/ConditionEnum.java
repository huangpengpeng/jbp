package com.jbp.service.condition;

import com.beust.jcommander.internal.Lists;
import lombok.Getter;

import java.util.List;

/**
 * 升级条件枚举
 */
public enum ConditionEnum {

    一阶人数升级("一阶人数升级", "等级", "直接邀请人数等级达到后进行升级"),
    补差金额升级("补差金额升级", "等级","用户当前等级一次性订货金额与下一个等级一次性订货金额差额达到即可升级"),
    单笔金额升级("单笔金额升级", "等级","单笔订单支付金额达到指定金额即可升级"),
    业绩独立线升星("业绩独立线升星", "星级","小区业绩加独立线人数星级满足邀请后升级"),
    ;

    @Getter
    private final String name;

    @Getter
    private final String type;

    @Getter
    private final String description;

    ConditionEnum(String name, String type, String description) {
        this.name = name;
        this.type = type;
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
