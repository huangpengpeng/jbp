package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.PlatformIntegralRecord;
import com.jbp.common.request.IntegralPageSearchRequest;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.IntegralRecordVo;
import com.jbp.service.dao.PlatformIntegralRecordDao;
import com.jbp.service.service.PlatformIntegralRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PlatformIntegralRecordServiceImpl extends ServiceImpl<PlatformIntegralRecordDao, PlatformIntegralRecord> implements PlatformIntegralRecordService {
    @Override
    public PlatformIntegralRecord add(String integralType, String externalNo, Integer type,
                                      String title, BigDecimal integral, BigDecimal balance, String postscript) {
        PlatformIntegralRecord record = new PlatformIntegralRecord(integralType, externalNo, type, title, integral, balance, postscript);
        save(record);
        return record;
    }

    @Override
    public PageInfo<IntegralRecordVo> page(IntegralPageSearchRequest request, PageParamRequest pageRequest) {
        return null;
    }
}
