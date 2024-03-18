package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.agent.WalletWithdraw;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface WalletWithdrawDao extends BaseMapper<WalletWithdraw> {
    List<WalletWithdraw> pageList(@Param("account") String account,@Param("walletName") String walletName,@Param("status") String status,
                                 @Param("endTime") String endTime,@Param("startTime")  String startTime,@Param("realName") String realName,@Param("channelName") String channelName);

}
