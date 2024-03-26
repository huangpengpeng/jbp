package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.system.JushuitanConfig;


public interface JushuitanConfigService extends IService<JushuitanConfig> {


    JushuitanConfig def();

    JushuitanConfig add(String accessToken, String appKey, String appSecret, String cancelCallApi, Long expiresIn, String refreshToken, String refundCallApi,
                        String repCallApi, String scope, String shipCallApi, String shopId);




}
