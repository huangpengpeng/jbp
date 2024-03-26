package com.jbp.service.dao.agent;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.agent.WalletWithdraw;
import com.jbp.common.vo.WalletWithdrawVo;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface WalletWithdrawDao extends BaseMapper<WalletWithdraw> {
    List<WalletWithdraw> pageList(@Param("account") String account, @Param("walletName") String walletName, @Param("status") String status,
                                  @Param("endTime") String endTime, @Param("startTime") String startTime, @Param("realName") String realName,
                                  @Param("channelName") String channelName,@Param("nickName")String nickName);

    List<WalletWithdrawVo> excel(@Param("id") Integer id,@Param("account") String account,@Param("walletName") String walletName,@Param("status") String status,
                                 @Param("realName") String realName, @Param("startTime") String startTime,@Param("endTime") String endTime,
                                 @Param("channelName") String channelName ,@Param("nickName")String nickName);
}
