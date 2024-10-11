package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.UserJd;
import com.jbp.service.dao.UserJdDao;
import com.jbp.service.service.UserJdService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class UserJdServiceImpl extends ServiceImpl<UserJdDao, UserJd> implements UserJdService {

    @Resource
    private UserJdDao dao;


}

