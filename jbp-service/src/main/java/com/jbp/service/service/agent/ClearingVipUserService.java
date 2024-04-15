package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingVipUser;

import java.math.BigDecimal;

public interface ClearingVipUserService extends IService<ClearingVipUser> {

    ClearingVipUser create(Integer uid, String accountNo, Long level, String levelName,
                           Integer commType, String commName, BigDecimal maxAmount, String rule, String description);




}
