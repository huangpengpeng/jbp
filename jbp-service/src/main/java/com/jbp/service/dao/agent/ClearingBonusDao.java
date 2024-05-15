package com.jbp.service.dao.agent;

import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.mybatis.RootMapper;
import com.jbp.common.response.ClearingBonusListResponse;

import java.util.List;

public interface ClearingBonusDao extends RootMapper<ClearingBonus> {

    List<ClearingBonusListResponse> getClearingList(Integer uid);

}
