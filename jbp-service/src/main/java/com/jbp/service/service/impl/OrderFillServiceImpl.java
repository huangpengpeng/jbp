package com.jbp.service.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.OrderFillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class OrderFillServiceImpl extends ServiceImpl<OrderFillDao, OrderFill> implements OrderFillService {

    private static final Logger logger = LoggerFactory.getLogger(OrderFillService.class);

    @Resource
    private OrderFillDao dao;

    @Override
    public PageInfo<OrderFill> getList(Integer uid, String oNickname, String orderNo, PageParamRequest pageParamRequest) {
        Page<OrderFill> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<OrderFill> list = dao.getList(uid, oNickname, orderNo);
        return CommonPage.copyPageInfo(page, list);
    }
}

