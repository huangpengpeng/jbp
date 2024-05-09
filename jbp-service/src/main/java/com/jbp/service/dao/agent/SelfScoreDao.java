package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.SelfScore;
import io.lettuce.core.dynamic.annotation.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SelfScoreDao extends BaseMapper<SelfScore> {


    BigDecimal getUserNext(Integer uid);


    List<SelfScore> getTeamUserScore(@Param("uid")Integer uid , @Param("startPayTime")String startPayTime,@Param("endPayTime") String endPayTime);


}
