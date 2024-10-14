package com.jbp.service.service.pay.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.pay.PayChannelDao;
import com.jbp.service.service.pay.PayChannelMng;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayChannelMngImpl extends ServiceImpl<PayChannelDao, PayChannel> implements PayChannelMng {
    @Override
    public PageInfo<PayChannel> page(PageParamRequest pageParamRequest, String name) {
        Page<PayChannel> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayChannel> q = new LambdaQueryWrapper<>();
        q.like(StringUtils.isNotEmpty(name), PayChannel::getName, name)
                .orderByAsc(PayChannel::getId);
        return CommonPage.copyPageInfo(page, list(q));
    }
}
