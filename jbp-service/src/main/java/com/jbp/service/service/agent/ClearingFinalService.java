package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ClearingRequest;

import java.math.BigDecimal;
import java.util.Set;

public interface ClearingFinalService extends IService<ClearingFinal> {

    ClearingFinal oneKeyClearing(ClearingRequest clearingRequest);

    void syncOneKeyClearing(ClearingRequest clearingRequest);

    Boolean oneKeyDel(Long clearingId);

    Boolean oneKeySend(Long clearingId);

    ClearingFinal create(String commName, Integer commType, String startTime, String endTime, BigDecimal adjustScore);

    ClearingFinal getByName(String name);

    /**
     * 获取上一次结算
     */
    ClearingFinal getLastOne(Long id, Integer commType);

    PageInfo<ClearingFinal> pageList(Integer commType, String status, PageParamRequest pageParamRequest);
}
