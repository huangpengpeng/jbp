package com.jbp.service.dao.agent;

import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.mybatis.RootMapper;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ClearingBonusListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClearingBonusDao extends RootMapper<ClearingBonus> {

    public List<ClearingBonusListResponse> getcleringList(Integer uid);

    ClearingBonusListResponse getcleringInfoList( @Param("uid")Integer uid , @Param("day")String day);
}
