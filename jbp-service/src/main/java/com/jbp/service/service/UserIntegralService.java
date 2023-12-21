package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserIntegral;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户积分账户
 */
public interface UserIntegralService extends IService<UserIntegral> {

    UserIntegral add(Integer uid, String type);

    UserIntegral getByUser(Integer uid, String type);

    void increase(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript);

    void reduce(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript);

    void transferToPlatform(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript);
}
