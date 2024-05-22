package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.Wallet;
import com.jbp.common.response.WalletExtResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WalletDao extends BaseMapper<Wallet> {


    List<WalletExtResponse> getList(@Param("uId") Integer uid, @Param("type") Integer type, @Param("teamId")String teamId, @Param("nickname")String nickname);
}
