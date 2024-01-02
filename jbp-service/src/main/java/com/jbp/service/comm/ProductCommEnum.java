package com.jbp.service.comm;

import lombok.Getter;


public enum ProductCommEnum {

    直推佣金(1, "直推佣金", "自己达到相应的等级要求，直接邀请人下单根据下单产品佣金设置规则进行分佣"),
    推三返一(2, "推三返一", "每直接推荐一单设置推三返一的商品，根据推荐单数三单一循环获取商品推三返一的佣金");

    /**
     * 佣金类型唯一
     */
    @Getter
    private final Integer type;

    /**
     * 佣金名称  唯一
     */
    @Getter
    private final String name;

    /**
     * 佣金描述 一定要写清楚 不要在乎文字长短  要在佣金设置当注意事项来用
     */
    @Getter
    private final String desc;

    ProductCommEnum(Integer type, String name, String desc) {
        this.type = type;
        this.name = name;
        this.desc = desc;
    }
}
