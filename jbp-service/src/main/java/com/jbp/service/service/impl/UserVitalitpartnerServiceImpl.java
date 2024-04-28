package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.UserVitalitpartner;
import com.jbp.service.dao.UserVisaDao;
import com.jbp.service.dao.UserVitalitpartnerDao;
import com.jbp.service.service.UserVitalitpartnerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class UserVitalitpartnerServiceImpl extends ServiceImpl<UserVitalitpartnerDao, UserVitalitpartner> implements UserVitalitpartnerService {

    @Resource
    private UserVisaDao dao;

}

