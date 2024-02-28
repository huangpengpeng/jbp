package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.user.User;
import com.jbp.service.dao.OrderExtDao;
import com.jbp.service.service.OrderExtService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderExtServiceImpl extends ServiceImpl<OrderExtDao, OrderExt> implements OrderExtService {
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
}
