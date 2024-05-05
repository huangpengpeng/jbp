package com.jbp.service.product.comm;

import lombok.Getter;

/**
 * 佣金别名前台佣金明细展示
 */
public enum CommAliasNameEnum {

    直推佣金("分享佣金"),
    推三返一( "推三返一"),
    渠道佣金("渠道佣金"),
    深度佣金("深度佣金"),
    社群佣金("社群佣金"),
    管理佣金("管理佣金"),
    店铺佣金("店铺佣金"),
    间推佣金("间推佣金"),
    星级直推佣金("星级直推佣金"),
    级差佣金("级差佣金"),
    平级佣金("平级佣金"),
    见点佣金("见点佣金"),
    推荐店铺佣金("分享店铺佣金"),
    分组推三返一("销售提成"),
    星级级差佣金("经销扣率"),
    级差伯乐佣金("培训基金"),
    星级级差佣金无伯乐("经销扣率"),
    ;
    @Getter
    private final String aliasName;
    ;

    CommAliasNameEnum(String aliasName) {
        this.aliasName = aliasName;

    }

    public static String getAliasNameByName(String name) {
        try {
            CommAliasNameEnum commAliasNameEnum = CommAliasNameEnum.valueOf(name);
            return commAliasNameEnum == null ? name : commAliasNameEnum.getAliasName();
        } catch (Exception e) {
            return name;
        }
    }
}
