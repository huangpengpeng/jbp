package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.response.UserVisaResponse;
import com.jbp.service.dao.UserVisaDao;
import com.jbp.service.service.UserVisaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class UserVisaServiceImpl extends ServiceImpl<UserVisaDao, UserVisa> implements UserVisaService {

    @Resource
    private UserVisaDao dao;


    @Override
    public void updateVisa(Integer id, String platform) {
        dao.updateVisa(id,platform);
    }

    @Override
    public UserVisaResponse getVisaTask(String signTaskId) {
        return   dao.getVisaTask(signTaskId);
    }
}

