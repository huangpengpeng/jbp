package com.jbp.service.product.comm;

import lombok.Getter;


public enum ProductCommEnum {

    直推佣金(1, "直推佣金", false,"自己达到相应的等级要求，直接邀请人下单根据下单产品佣金设置规则进行分佣"),
    推三返一(2, "推三返一", false,"每直接推荐一单设置推三返一的商品，根据推荐单数三单一循环获取商品推三返一的佣金"),
    渠道佣金(3, "渠道佣金", true, "双轨根据服务关系往上增加积分，对碰积分，在根据指定等级比例获得奖金金额"),
    深度佣金(4, "深度佣金", true, "获得渠道佣金的用户根据对碰积分份额参照自身等级比例获得奖金"),
    社群佣金(5, "社群佣金", true,"按销售关系,根据星级等级设置响应的佣金比例，截止最大比例分完"),
    管理佣金(6, "管理佣金", true,"社群佣金获得者根据销售关系往上找指定层数每层获得指定比例"),
    店铺佣金(7, "店铺佣金", true, "根据服务关系往上找最近的一个开店用户根据指定比例获得佣金"),
    间推佣金(8, "间推佣金", false, "自己达到相应的等级要求，间接邀请人下单根据下单产品佣金设置规则进行分佣"),
    星级直推佣金(9, "星级直推佣金", false,"自己达到相应的星级要求，直接邀请人下单根据下单产品佣金设置规则进行分佣"),
    级差佣金(10, "级差佣金", false,"根据下单人(不考虑下单人等级)往上找到指定等级用户根据配置比例进行分钱"),
    平级佣金(11, "平级佣金", false,"获取级差佣金用户与下一个级差佣金获得者中间与上一个级差获得者等级相同即可获得"),
    见点佣金(12, "见点佣金", false,"下单人往上找指定人数根据配置的等级比例分佣"),
    星级级差佣金(13, "星级级差佣金", false,"根据下单人(不考虑下单人星级)往上找到指定星级用户根据配置比例进行分钱"),
    级差伯乐佣金(14, "级差伯乐佣金", true,"根据获得星级级差将的用户往上找指定等级规定人数按照配置的比例获得佣金"),
    推荐店铺佣金(15, "推荐店铺佣金", true, "获得店铺佣金的上级用户店铺佣金指定比例金额"),
    分组推三返一(16, "分组推三返一", false,"（艾培生）每直接推荐一单设置分组推三返一的商品，根据分组不同商品推荐单数三单一循环获取商品推三返一的佣金"),
    ;
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
     * 全局配置是不需要配置在商品上
     */
    @Getter
    private final Boolean ifWhole;

    /**
     * 佣金描述 一定要写清楚 不要在乎文字长短  要在佣金设置当注意事项来用
     */
    @Getter
    private final String desc;


    ProductCommEnum(Integer type, String name, Boolean ifWhole, String desc) {
        this.type = type;
        this.name = name;
        this.ifWhole = ifWhole;
        this.desc = desc;
    }

    public static String getCommName(Integer type) {
        for (ProductCommEnum value : ProductCommEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getName();
            }
        }
        return null;
    }
}
