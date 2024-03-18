package com.jbp.common.kqbill.utils;

import com.jbp.common.kqbill.contants.Bill99ConfigInfo;
import org.apache.commons.lang3.StringUtils;

public class RequestUrLoader {
    /**
     * 根据messageType及请求环境指定请求地址
     */
    public static String getRequestUrl(String messageType , String env){
        if(StringUtils.isEmpty(messageType) || StringUtils.isEmpty(env) ){
            throw new NullPointerException("messageType或请求环境配置不能为空！");
        }
        String messageTypePrefix = Bill99ConfigInfo.UMGW_BOSS_MESSAGETYPE_PREFIX;
        if( matchedWithPrefix(messageType,messageTypePrefix) && "sandbox".equals(env) ){
            return Bill99ConfigInfo.UMGW_BOSS_SANDBOX_URL;
        }else if(matchedWithPrefix(messageType,messageTypePrefix) && "prod".equals(env)){
            return Bill99ConfigInfo.UMGW_BOSS_PROD_URL;
        }else if(!matchedWithPrefix(messageType,messageTypePrefix) && "sandbox".equals(env)){
            return Bill99ConfigInfo.UMGW_SANDBOX_URL;
        }else if(!matchedWithPrefix(messageType,messageTypePrefix) && "prod".equals(env)){
            return Bill99ConfigInfo.UMGW_PROD_URL;
        }
        return null;
    }

    private static boolean matchedWithPrefix( String messageType ,String messageTypePrefix ){
        return StringUtils.startsWithAny(messageType,messageTypePrefix.split(","));
    }

}
