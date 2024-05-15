package com.jbp.service.dao.agent;

import com.jbp.common.model.agent.ClearingBonusFlow;
import com.jbp.common.mybatis.RootMapper;
import com.jbp.common.response.ClearingBonusListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClearingBonusFlowDao extends RootMapper<ClearingBonusFlow> {

    List<ClearingBonusListResponse> getClearingInfoList(@Param("uid") Integer uid, @Param("day") String day);
}
