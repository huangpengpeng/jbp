package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.PlatformIntegralRecord;

import java.math.BigDecimal;

public interface PlatformIntegralRecordService extends IService<PlatformIntegralRecord> {
    PlatformIntegralRecord add(String integralType, String externalNo, Integer type, String title, BigDecimal integral, BigDecimal balance, String postscript);

}
