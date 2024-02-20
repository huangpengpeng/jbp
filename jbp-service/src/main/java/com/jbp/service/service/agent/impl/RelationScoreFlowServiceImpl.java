package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.service.dao.agent.RelationScoreFlowDao;
import com.jbp.service.service.agent.RelationScoreFlowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreFlowServiceImpl extends ServiceImpl<RelationScoreFlowDao, RelationScoreFlow> implements RelationScoreFlowService {
}
