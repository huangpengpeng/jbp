package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.PlatformIntegral;
import com.jbp.common.model.user.PlatformIntegralRecord;

import java.math.BigDecimal;

public interface PlatformIntegralService extends IService<PlatformIntegral> {
    PlatformIntegral get(Integer type);

    PlatformIntegral add(Integer type);

    void increase(Integer integralType, String externalNo, String title, BigDecimal integral, String postscript);

    void reduce(Integer integralType, String externalNo, String title, BigDecimal integral, String postscript);

    void transferToUser(Integer uid, Integer integralType, String externalNo, String title, BigDecimal integral, String postscript);



}
