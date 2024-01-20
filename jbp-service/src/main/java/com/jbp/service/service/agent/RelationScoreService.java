package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.RelationScore;

public interface RelationScoreService extends IService<RelationScore> {
    RelationScore getByUser(Integer uId, Integer node);
}
