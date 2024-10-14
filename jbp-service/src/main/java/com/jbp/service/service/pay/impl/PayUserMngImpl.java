package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.pay.PayUser;
import com.jbp.service.dao.pay.PayUserDao;
import com.jbp.service.service.pay.PayUserMng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayUserMngImpl extends ServiceImpl<PayUserDao, PayUser> implements PayUserMng {
    @Override
    public PayUser getByAppKey(String appKey) {
        return getOne(new LambdaQueryWrapper<PayUser>().eq(PayUser::getAppKey, appKey));
    }
}
