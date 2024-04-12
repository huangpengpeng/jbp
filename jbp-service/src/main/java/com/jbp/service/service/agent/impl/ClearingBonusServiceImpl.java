package com.jbp.service.service.agent.impl;

import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.service.dao.agent.ClearingBonusDao;
import com.jbp.service.service.agent.ClearingBonusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingBonusServiceImpl extends UnifiedServiceImpl<ClearingBonusDao, ClearingBonus> implements ClearingBonusService {

    @Resource
    private ClearingBonusDao dao;

    @Override
    public void insertBatchList(List<ClearingBonus> list) {
        dao.insertBatch(list);
    }


}

