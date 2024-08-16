package com.jbp.service.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.OrderFillService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.CapaOrderService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
public class OrderFillServiceImpl extends ServiceImpl<OrderFillDao, OrderFill> implements OrderFillService {

    private static final Logger logger = LoggerFactory.getLogger(OrderFillService.class);

    @Resource
    private OrderFillDao dao;
    @Resource
    private OrderService orderService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private CapaOrderService capaOrderService;

    @Override
    public OrderFill add(String orderNo, Integer uId) {

        OrderFill orderFill = new OrderFill();
        orderFill.setCreateTime(new Date());
        orderFill.setExpiredTime(DateTimeUtils.addHours(new Date(), 72));
        orderFill.setOrderNo(orderNo);
        orderFill.setStatus("待补单");
        orderFill.setUId(uId);
        save(orderFill);
        return orderFill;
    }

    @Override
    public void saveOrder(String orderNo) {

        Order order = orderService.getByOrderNo(orderNo);

        UserCapa userCapa = userCapaService.getByUser(order.getUid());
        CapaOrder capaOrder = capaOrderService.getByCapaId(userCapa.getCapaId().intValue());
        //公司供货不补单
        if (capaOrder.getIfCompany()) {
            return;
        }
        //获取补单权限的用户
        UserInvitation userInvitation = userInvitationService.getByUser(order.getUid());

        do {
            if(userInvitation == null){
                break;
            }
            UserCapa pCapa = userCapaService.getByUser(userInvitation.getPId());
            CapaOrder pCapaOrder = capaOrderService.getByCapaId(pCapa.getCapaId().intValue());

            if (pCapaOrder.getIfSupply()) {
                add(orderNo, userInvitation.getPId());
                break;
            }
            userInvitation = userInvitationService.getByUser(userInvitation.getPId());


        } while (true);


    }

    @Override
    public PageInfo<OrderFill> getList(Integer uid, String oNickname, String orderNo, PageParamRequest pageParamRequest) {
        Page<OrderFill> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<OrderFill> list = dao.getList(uid, oNickname, orderNo);
        return CommonPage.copyPageInfo(page, list);
    }
}

