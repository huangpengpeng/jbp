package com.jbp.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserVisa;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.common.response.UserVisaRecordResponse;
import com.jbp.common.response.UserVisaResponse;
import com.jbp.service.dao.UserVisaDao;
import com.jbp.service.service.UserVisaService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    @Override
    public PageInfo<UserVisaRecordResponse> pageList(String account, PageParamRequest pageParamRequest) {


        Page<UserVisaRecordResponse> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserVisaRecordResponse> activateInfoResponses = dao.getAdminPageList(account);

        return CommonPage.copyPageInfo(page, activateInfoResponses);



    }
}

