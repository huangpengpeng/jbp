package com.jbp.service.product.profit;

import lombok.Getter;

/**
 * 购买商品收益枚举
 */
public enum ProductProfitEnum {
    等级(1, "直升等级", "购买当前商品升级等级"),
    星级(2, "直升星级", "购买当前商品升级星级"),
    白名单(3, "添加白名单", "购买完当前商品添加用户白名单"),
    积分(4, "奖励积分", "购买完当前商品奖励积分"),
    活跃(5, "更新活跃", "购买完当前商品奖励活跃有效期");

    @Getter
    private final Integer type;

    @Getter
    private final String name;

    /**
     * 佣金描述 一定要写清楚 不要在乎文字长短  要在佣金设置当注意事项来用
     */
    @Getter
    private final String desc;

    ProductProfitEnum(Integer type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;
    }
}
