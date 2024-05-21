package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserMonthActiveResponse;

import java.math.BigDecimal;

public interface ClearingVipUserService extends IService<ClearingVipUser> {

    ClearingVipUser create(Integer uid, String accountNo, Long level, String levelName,
                           Integer commType, String commName, BigDecimal maxAmount, String rule, String description);

    ClearingVipUser getByUser(Integer uid, Long level, Integer commType);

    PageInfo<ClearingVipUser> pageList(Integer uid, Integer status,Long level,Integer commType,PageParamRequest pageParamRequest);

    UserMonthActiveResponse getActive(Integer uid);




}
