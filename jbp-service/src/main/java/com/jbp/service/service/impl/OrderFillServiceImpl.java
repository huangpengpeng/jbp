package com.jbp.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.OrderFillService;
import com.jbp.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OrderFillServiceImpl extends ServiceImpl<OrderFillDao, OrderFill> implements OrderFillService {

    private static final Logger logger = LoggerFactory.getLogger(OrderFillService.class);

    @Resource
    private OrderFillDao dao;
    @Resource
    private UserService userService;

    @Override
    public PageInfo<OrderFill> getList(Integer uid, String oNickname, String orderNo, PageParamRequest pageParamRequest) {
        Page<OrderFill> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<OrderFill> list = dao.getList(uid, oNickname, orderNo);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Map<String, OrderFill> getOrderNoMapList(List<String> orderNoList, String status) {
        LambdaQueryWrapper<OrderFill> lqw = new LambdaQueryWrapper<>();
        lqw.in(OrderFill::getOrderNo, orderNoList);
        lqw.eq(OrderFill::getStatus, status);
        List<OrderFill> list = list(lqw);
        Map<String, OrderFill> orderFillMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)){
            return orderFillMap;
        }
        List<Integer> uidList = list.stream().map(OrderFill::getUid).collect(Collectors.toList());
        Map<Integer, User> uidMapList = userService.getUidMapList(uidList);
        for (OrderFill orderFill : list) {
            User user = uidMapList.get(orderFill.getUid());
            orderFill.setSAccount(user != null ? user.getAccount() : "");
            orderFill.setSNickname(user != null ? user.getNickname() : "");
            orderFillMap.put(orderFill.getOrderNo(), orderFill);
        }
        return orderFillMap;
    }
}

