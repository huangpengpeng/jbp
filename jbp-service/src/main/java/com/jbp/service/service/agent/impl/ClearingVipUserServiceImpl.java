package com.jbp.service.service.agent.impl;

import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.service.dao.agent.ClearingVipUserDao;
import com.jbp.service.service.agent.ClearingVipUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingVipUserServiceImpl extends UnifiedServiceImpl<ClearingVipUserDao, ClearingVipUser> implements ClearingVipUserService {
    @Override
    public ClearingVipUser create(Integer uid, String accountNo, Long level, String levelName,
                                  Integer commType, String commName, BigDecimal maxAmount, String rule, String description) {
        ClearingVipUser user = new ClearingVipUser(uid, accountNo, level, levelName, commType, commName, maxAmount, rule, description);
        save(user);
        return user;
    }

}
