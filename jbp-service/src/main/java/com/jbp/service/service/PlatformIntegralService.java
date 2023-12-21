package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.PlatformIntegral;
import com.jbp.common.model.user.PlatformIntegralRecord;

import java.math.BigDecimal;

public interface PlatformIntegralService extends IService<PlatformIntegral> {
    PlatformIntegral get(String type);

    PlatformIntegral add(String type);

    void increase(String integralType, String externalNo, String title, BigDecimal integral, String postscript);

    void reduce(String integralType, String externalNo, String title, BigDecimal integral, String postscript);

    void transferToUser(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript);



}
