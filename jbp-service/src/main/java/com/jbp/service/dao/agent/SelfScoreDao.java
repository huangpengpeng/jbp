package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.SelfScore;

import java.math.BigDecimal;

public interface SelfScoreDao extends BaseMapper<SelfScore> {


    BigDecimal getUserNext(Integer uid);

}
