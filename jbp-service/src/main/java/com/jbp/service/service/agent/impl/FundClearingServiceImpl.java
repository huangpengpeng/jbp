package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.service.dao.agent.FundClearingDao;
import com.jbp.service.service.agent.FundClearingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingServiceImpl extends ServiceImpl<FundClearingDao, FundClearing> implements FundClearingService {
}
