package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.PlatformIntegralRecord;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.IntegralRecordVo;

import java.math.BigDecimal;

public interface PlatformIntegralRecordService extends IService<PlatformIntegralRecord> {
    PlatformIntegralRecord add(Integer integralType, String externalNo, Integer type, String title, BigDecimal integral, BigDecimal balance, String postscript);


    PageInfo<IntegralRecordVo> page(IntegralPageSearchRequest request, PageParamRequest pageRequest);
}
