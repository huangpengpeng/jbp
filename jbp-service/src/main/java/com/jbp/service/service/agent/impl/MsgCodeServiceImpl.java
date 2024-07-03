package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.MsgCode;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.MsgCodeDao;
import com.jbp.service.service.agent.MsgCodeService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class MsgCodeServiceImpl extends ServiceImpl<MsgCodeDao, MsgCode> implements MsgCodeService {

    @Override
    public PageInfo<MsgCode> page(String phone, PageParamRequest pageParamRequest) {
        Page<Capa> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<MsgCode> list = list(new QueryWrapper<MsgCode>().lambda().eq(StringUtils.isNotEmpty(phone), MsgCode::getPhone, phone).orderByDesc(MsgCode::getId));
        return CommonPage.copyPageInfo(page, list);
    }
}
