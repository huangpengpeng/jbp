package com.jbp.service.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;

import com.jbp.common.constants.WeChatConstants;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.impl.SystemConfigServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName CrmebWxConfiguration
 * @Description 微信配置（wxJava）
 * @Author HZW
 * @Date 2023/4/3 16:28
 * @Version 1.0
 */
@Slf4j
@Component
public class CrmebWxConfiguration {

    private static final SystemConfigService systemConfigService;

    static {
        systemConfigService = new SystemConfigServiceImpl();
    }


    /**
     * 获取微信小程序服务
     * WxMaService wxMaService = CrmebWxConfiguration.getWxMaService();
     * ...
     * WxMaConfigHolder.remove();//清理ThreadLocal
     */
    public static WxMaService getWxMaService() {
        String appid = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
        String secret = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPSECRET);
        String token = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_TOKEN);
        String aesKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_AES_KEY);
        String msgDataFormat = WeChatConstants.WECHAT_MINI_MSG_DATA_FORMAT;
        WxMaDefaultConfigImpl wxMaConfig = new WxMaDefaultConfigImpl();
        wxMaConfig.setAppid(appid);
        wxMaConfig.setSecret(secret);
        wxMaConfig.setToken(token);
        wxMaConfig.setAesKey(aesKey);
        wxMaConfig.setMsgDataFormat(msgDataFormat);
        WxMaServiceImpl wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(wxMaConfig);
        return wxMaService;
    }

}
