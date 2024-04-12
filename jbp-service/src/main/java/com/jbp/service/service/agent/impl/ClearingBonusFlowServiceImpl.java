package com.jbp.service.service.agent.impl;

import com.jbp.common.model.agent.ClearingBonusFlow;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.service.dao.agent.ClearingBonusFlowDao;
import com.jbp.service.service.agent.ClearingBonusFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingBonusFlowServiceImpl extends UnifiedServiceImpl<ClearingBonusFlowDao, ClearingBonusFlow> implements ClearingBonusFlowService {

    @Resource
    private ClearingBonusFlowDao dao;

    @Override
    public void insertBatchList(List<ClearingBonusFlow> list) {
        dao.insertBatch(list);
    }
}
