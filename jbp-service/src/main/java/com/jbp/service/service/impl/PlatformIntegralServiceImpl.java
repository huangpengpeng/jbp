package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.PlatformIntegral;
import com.jbp.common.model.user.PlatformIntegralRecord;
import com.jbp.service.dao.PlatformIntegralDao;
import com.jbp.service.dao.PlatformIntegralRecordDao;
import com.jbp.service.service.PlatformIntegralRecordService;
import com.jbp.service.service.PlatformIntegralService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PlatformIntegralServiceImpl extends ServiceImpl<PlatformIntegralDao, PlatformIntegral> implements PlatformIntegralService {

    @Override
    public PlatformIntegral get(String type) {
        return null;
    }

    @Override
    public PlatformIntegral add(String type) {
        return null;
    }

    @Override
    public void increase(String integralType, String externalNo, String title, BigDecimal integral, String postscript) {

    }

    @Override
    public void reduce(String integralType, String externalNo, String title, BigDecimal integral, String postscript) {

    }

    @Override
    public void transferToUser(Integer uid, String integralType, String externalNo, String title, BigDecimal integral, String postscript) {

    }
}
