package com.jbp.service.service.agent.impl;

import com.jbp.common.model.agent.ClearingUser;
import com.jbp.common.mybatis.UnifiedServiceImpl;
import com.jbp.service.dao.agent.ClearingUserDao;
import com.jbp.service.service.agent.ClearingUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ClearingUserServiceImpl extends UnifiedServiceImpl<ClearingUserDao, ClearingUser> implements ClearingUserService {


    @Override
    public Boolean importUserList(Long clearingId, List<String> account) {
        return null;
    }

    @Override
    public Boolean init(Long clearingId) {
        return null;
    }

    @Override
    public BigDecimal progress(Long clearingId) {
        return null;
    }

    @Override
    public Boolean del4Clearing(Long clearingId) {
        return null;
    }

    @Override
    public Boolean del(Long id) {
        return null;
    }

    @Override
    public Boolean add(String account) {
        return null;
    }

    @Override
    public Boolean edit(Long id, Long capaId, Long capaXsId) {
        return null;
    }
}
