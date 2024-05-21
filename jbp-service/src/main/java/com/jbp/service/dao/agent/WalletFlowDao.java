package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.mybatis.RootMapper;
import com.jbp.common.response.WalletFlowExtResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WalletFlowDao extends RootMapper<WalletFlow> {

    List<WalletFlowExtResponse>  getList(@Param("uId")Integer uid, @Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("externalNo") String externalNo, @Param("action") String action,  @Param("teamId") String teamId);

}
