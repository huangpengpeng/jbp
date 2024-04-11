package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.service.dao.agent.ClearingFinalDao;
import com.jbp.service.service.agent.ClearingFinalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingFinalServiceImpl extends UnifiedServiceImpl<ClearingFinalDao, ClearingFinal> implements ClearingFinalService {

    @Override
    public ClearingFinal create(String commName, String startTime, String endTime) {
        String name = commName + "_" + startTime + "-" + endTime;
        ClearingFinal clearingFinal = getByName(name);
        if (clearingFinal != null) {
            throw new CrmebException(commName + "结算开始-结束,时间周期已经存在请勿重复操作");
        }
        clearingFinal = ClearingFinal.builder().name(name).commName(commName).startTime(startTime)
                .endTime(endTime).status(ClearingFinal.Constants.待结算.name()).totalScore(BigDecimal.ZERO).totalAmt(BigDecimal.ZERO).build();
        save(clearingFinal);
        return clearingFinal;
    }

    @Override
    public ClearingFinal getByName(String name) {
        return getOne(new QueryWrapper<ClearingFinal>().lambda().eq(ClearingFinal::getName, name));
    }

    @Override
    public ClearingFinal getLastOne(Long id) {
        return getOne(new QueryWrapper<ClearingFinal>().lambda().lt(ClearingFinal::getId, id).last(" limit 1"));
    }
}
