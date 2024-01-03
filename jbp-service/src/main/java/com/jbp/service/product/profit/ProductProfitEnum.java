package com.jbp.service.product.profit;

import lombok.Getter;

/**
 * 购买商品收益枚举
 */
public enum ProductProfitEnum {
    等级(1, true, "赠送代理商等级"),
    星级(2, true, "赠送代理商星级"),
    白名单(3, false, "赠送代理商白名单"),
    积分(4, true, "赠送代理商积分"),
    活跃(5, true, "赠送代理活跃到期");;

    @Getter
    private final Integer type;

    @Getter
    private final Boolean ifShow;

    @Getter
    private final String name;

    ProductProfitEnum(Integer type, Boolean ifShow, String name) {
        this.type = type;
        this.ifShow = ifShow;
        this.name = name;
    }
}
