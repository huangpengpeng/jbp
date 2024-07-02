package com.jbp.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrderExtProductListRequest;
import com.jbp.common.response.OrderExtProductResponse;
import com.jbp.common.utils.CrmebDateUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.common.vo.DateLimitUtilVo;
import com.jbp.service.dao.OrderExtDao;
import com.jbp.service.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderExtServiceImpl extends ServiceImpl<OrderExtDao, OrderExt> implements OrderExtService {

    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private RefundOrderService refundOrderService;
    @Override
    public OrderExt getByOrder(String orderNo) {
        return getOne(new QueryWrapper<OrderExt>().lambda().eq(OrderExt::getOrderNo, orderNo));
    }

    @Override
    public Map<String, OrderExt> getOrderNoMapList(List<String> orderNoList) {
        LambdaQueryWrapper<OrderExt> lqw = new LambdaQueryWrapper<>();
        lqw.in(OrderExt::getOrderNo, orderNoList);
        List<OrderExt> userList =list(lqw);
        Map<String, OrderExt> userMap = new HashMap<>();
        userList.forEach(e -> {
            userMap.put(e.getOrderNo(), e);
        });
        return userMap;
    }

    @Override
    public PageInfo<OrderExtProductResponse> getProductPage(OrderExtProductListRequest request, PageParamRequest pageParamRequest) {
        Page<Order> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<OrderExt> list = list(new QueryWrapper<OrderExt>().lambda().isNotNull(OrderExt::getOrderGoodsInfo));
        List<String> orderNoList = list.stream().map(OrderExt::getOrderNo).collect(Collectors.toList());
        if (orderNoList.isEmpty()){
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        LambdaQueryWrapper<Order> lqw = Wrappers.lambdaQuery();
            lqw.in(Order::getOrderNo, orderNoList);

        if (StrUtil.isNotBlank(request.getPayTime())) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(request.getPayTime());
            lqw.between(Order::getPayTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }

        if (StrUtil.isNotEmpty(request.getDateLimit())) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(Order::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        if (StringUtils.isNotEmpty(request.getProductName())) {
            lqw.apply(" order_no in ( select order_no from eb_order_detail where product_id in " +
                    "(select id from eb_product where name like '%"+request.getProductName()+"%') ) ");
        }
        if (StrUtil.isNotBlank(request.getOrderNo())) {
            lqw.and((wrapper) -> {
                wrapper.eq(Order::getOrderNo, request.getOrderNo())
                        .or().eq(Order::getPlatOrderNo, request.getOrderNo());
            });
        }
        lqw.orderByDesc(Order::getId);
        List<Order> orderList = orderService.list(lqw);
        if (CollUtil.isEmpty(orderList)) {
            return CommonPage.copyPageInfo(startPage, CollUtil.newArrayList());
        }
        List<Integer> uidList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        Map<String, List<OrderDetail>> mapByOrderNoList = orderDetailService.getMapByOrderNoList(orderNoList);

        List<OrderExtProductResponse> pageResponses = orderList.stream().map(e -> {
            OrderExtProductResponse response = new OrderExtProductResponse();
            response.setOrderNo(e.getOrderNo());
            response.setCreateTime(e.getCreateTime());
            response.setPayTime(e.getPayTime());
            response.setStatus(e.getStatus());
            response.setContent(getByOrder(e.getOrderNo()).getOrderGoodsInfo());
            User user = userMap.get(e.getUid());
            response.setNickname(user.getNickname());
            response.setAccount(user.getAccount());
            List<OrderDetail> orderDetails = mapByOrderNoList.get(e.getOrderNo());
            response.setProductName(orderDetails.isEmpty() ? "" : orderDetails.get(0).getProductName());
            response.setProductId(orderDetails.isEmpty() ? null : orderDetails.get(0).getProductId());
            if (e.getRefundStatus() == 3) {
                RefundOrder refundOrder = refundOrderService.getOne(new QueryWrapper<RefundOrder>().lambda().eq(RefundOrder::getOrderNo, e.getOrderNo()));
                response.setRefundTime(refundOrder==null ? null : refundOrder.getCreateTime());
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(startPage, pageResponses);

    }
}
