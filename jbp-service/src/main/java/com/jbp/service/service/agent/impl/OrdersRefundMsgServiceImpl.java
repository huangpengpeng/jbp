package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.OrdersRefundMsg;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.OrdersRefundMsgDao;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrdersRefundMsgServiceImpl extends ServiceImpl<OrdersRefundMsgDao, OrdersRefundMsg> implements OrdersRefundMsgService {
    @Override
    public OrdersRefundMsg create(String ordersSn, String refundSn, String context) {
        OrdersRefundMsg msg = new OrdersRefundMsg(ordersSn, refundSn, context);
        save(msg);
        return msg;
    }

    @Override
    public void read(List<Long> ids, String remark) {
        UpdateWrapper<OrdersRefundMsg> updateWrapper = new UpdateWrapper();
        updateWrapper.lambda().set(OrdersRefundMsg::getIfRead, true)
                .set(OrdersRefundMsg::getRemark, remark)
                .in(OrdersRefundMsg::getId, ids);
        update(updateWrapper);
    }

    @Override
    public PageInfo<OrdersRefundMsg> pageList(String ordersSn, String refundSn, Boolean ifRead, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<OrdersRefundMsg> lqw=new LambdaQueryWrapper<OrdersRefundMsg>()
                .like(StringUtils.isNotEmpty(ordersSn),OrdersRefundMsg::getOrdersSn,ordersSn)
                .like(StringUtils.isNotEmpty(refundSn),OrdersRefundMsg::getRefundSn,refundSn)
                .eq(!ObjectUtil.isNull(ifRead),OrdersRefundMsg::getIfRead,ifRead);
        Page<OrdersRefundMsg> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(lqw));
    }
}
