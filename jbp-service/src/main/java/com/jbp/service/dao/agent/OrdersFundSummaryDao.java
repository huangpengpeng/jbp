package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.OrdersFundSummary;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

public interface OrdersFundSummaryDao extends BaseMapper<OrdersFundSummary> {

    List<OrdersFundSummary> getList(@Param("teamId")String teamId, @Param("ordersSn")String ordersSn);

}
