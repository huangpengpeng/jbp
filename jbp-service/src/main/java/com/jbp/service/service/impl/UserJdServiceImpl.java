package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserJd;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.UserJdResponse;
import com.jbp.service.dao.UserJdDao;
import com.jbp.service.service.UserJdService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class UserJdServiceImpl extends ServiceImpl<UserJdDao, UserJd> implements UserJdService {

    @Resource
    private UserJdDao dao;


    @Override
    public PageInfo<UserJdResponse> getUserJdList(String account, String nickName, PageParamRequest pageRequest) {

        Page<UserJd> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<UserJdResponse> userJdResponseList = dao.getList(account, nickName);
        return CommonPage.copyPageInfo(page, userJdResponseList);
    }
}

