package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.service.dao.agent.RelationScoreDao;
import com.jbp.service.service.agent.RelationScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class RelationScoreServiceImpl extends ServiceImpl<RelationScoreDao, RelationScore> implements RelationScoreService {

    @Override
    public RelationScore getByUser(Integer uId, Integer node) {
        LambdaQueryWrapper<RelationScore> query = new LambdaQueryWrapper<>();
        query.eq(RelationScore::getUid, uId).eq(RelationScore::getNode, node);
        return getOne(query);
    }
}
