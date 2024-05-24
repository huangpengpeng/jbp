package com.jbp.service.product.comm;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 佣金别名前台佣金明细展示
 */
public enum CommAliasNameHdfEnum {

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
    推荐店铺佣金("推荐店铺佣金"),
    分组推三返一("分组推三返一"),
    星级级差佣金("星级级差佣金"),
    级差伯乐佣金("级差伯乐佣金"),
    星级级差佣金无伯乐("差价额外补贴"),
    见点奖("见点奖"),
    平级奖("平级奖"),
    级差奖("级差佣金"),
    重复消费积分("重复消费积分"),
    店务补贴("店务补贴"),
    升单极差("级差佣金"),
    ;
    @Getter
    private final String aliasName;
    ;

    CommAliasNameHdfEnum(String aliasName) {
        this.aliasName = aliasName;

    }

    public static String getAliasNameByName(String name) {
        try {
            CommAliasNameHdfEnum commAliasNameEnum = CommAliasNameHdfEnum.valueOf(name);
            return commAliasNameEnum == null ? name : commAliasNameEnum.getAliasName();
        } catch (Exception e) {
            return name;
        }
    }

    public static List<String> getListName() {
        List<String> list =new ArrayList<>();
        for(CommAliasNameHdfEnum commAliasNameSmEnum :   CommAliasNameHdfEnum.values()){
            list.add(commAliasNameSmEnum.getAliasName());
        }
            return list;
    }

    public static String getAliasNameReplaceName(String name){

        for(CommAliasNameHdfEnum commAliasNameSmEnum :   CommAliasNameHdfEnum.values()){
            if(name.contains(commAliasNameSmEnum.name())){
               name =  name.replace(commAliasNameSmEnum.name(),commAliasNameSmEnum.getAliasName());
            }
        }
        return name;
    }

}
