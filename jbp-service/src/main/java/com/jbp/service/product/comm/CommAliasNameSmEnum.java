package com.jbp.service.product.comm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金别名前台佣金明细展示
 */
public enum CommAliasNameSmEnum {

    直推佣金("分享佣金"),
    推三返一( "推三返一"),
    渠道佣金("渠道佣金"),
    深度佣金("深度佣金"),
    社群佣金("社群佣金"),
    管理佣金("管理佣金"),
    店铺佣金("店铺佣金"),
    间推佣金("间推佣金"),
    星级直推佣金("星级直推佣金"),
    级差佣金("产品积分"),
    平级佣金("服务积分"),
    见点佣金("推广积分"),
    推荐店铺佣金("分享店铺佣金"),
    分组推三返一("销售提成"),
    星级级差佣金("经销扣率"),
    级差伯乐佣金("培训基金"),
    星级级差佣金无伯乐("经销扣率"),
    见点奖("推广积分"),
    平级奖("服务积分"),
    级差奖("产品积分"),
    重复消费积分("服务补贴"),
    店务补贴("经销扣率"),
    ;
    @Getter
    private final String aliasName;
    ;

    CommAliasNameSmEnum(String aliasName) {
        this.aliasName = aliasName;

    }

    public static String getAliasNameByName(String name) {
        try {
            CommAliasNameSmEnum commAliasNameEnum = CommAliasNameSmEnum.valueOf(name);
            return commAliasNameEnum == null ? name : commAliasNameEnum.getAliasName();
        } catch (Exception e) {
            return name;
        }
    }

    public static List<String> getListName() {
        List<String> list =new ArrayList<>();
        for(CommAliasNameSmEnum commAliasNameSmEnum :   CommAliasNameSmEnum.values()){
            list.add(commAliasNameSmEnum.getAliasName());
        }
            return list;
    }

    public static String getAliasNameReplaceName(String name){

        for(CommAliasNameSmEnum commAliasNameSmEnum :   CommAliasNameSmEnum.values()){
            if(name.contains(commAliasNameSmEnum.name())){
               name =  name.replace(commAliasNameSmEnum.name(),commAliasNameSmEnum.getAliasName());
            }
        }
        return name;
    }

}
