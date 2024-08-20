package com.jbp.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.enums.OrderFillType;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderFillService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.ProductRepertoryService;
import com.jbp.service.service.agent.CapaOrderService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.extern.slf4j.Slf4j;
import com.jbp.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
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
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ProductRepertoryService productRepertoryService;

    @Override
    public OrderFill add(String orderNo, Integer uId) {

        OrderFill orderFill = new OrderFill();
        orderFill.setCreateTime(new Date());
        orderFill.setExpiredTime(DateTimeUtils.addHours(new Date(), 72));
        orderFill.setOrderNo(orderNo);
        orderFill.setStatus(OrderFillType.待补单.getName());
        orderFill.setUid(uId);
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
            if (userInvitation == null) {
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
    @Resource
    private UserService userService;

    @Override
    public PageInfo<OrderFill> getList(Integer uid, String oNickname, String orderNo, PageParamRequest pageParamRequest) {
        Page<OrderFill> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<OrderFill> list = dao.getList(uid, oNickname, orderNo);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public void expired(OrderFill orderFill) {

        orderFill.setStatus(OrderFillType.已拒补.getName());
        orderFill.setNoFillTime(new Date());
        dao.updateById(orderFill);

        Order order = orderService.getByOrderNo(orderFill.getOrderNo());

        UserCapa userCapa = userCapaService.getByUser(order.getUid());
        CapaOrder capaOrder = capaOrderService.getByCapaId(userCapa.getCapaId().intValue());
        //公司供货不补单
        if (capaOrder.getIfCompany()) {
            return;
        }
        //获取补单权限的用户
        UserInvitation userInvitation = userInvitationService.getByUser(orderFill.getUid());

        do {
            if (userInvitation == null) {
                break;
            }
            UserCapa pCapa = userCapaService.getByUser(userInvitation.getPId());
            CapaOrder pCapaOrder = capaOrderService.getByCapaId(pCapa.getCapaId().intValue());

            if (pCapaOrder.getIfSupply()) {
                add(orderFill.getOrderNo(), userInvitation.getPId());
                break;
            }
            userInvitation = userInvitationService.getByUser(userInvitation.getPId());


        } while (true);


    }


    @Override
    public void fill(OrderFill orderFill) {

        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderFill.getOrderNo());

        Boolean ifFill = false;
        for (OrderDetail orderDetail : orderDetailList) {
            ProductRepertory productRepertory = productRepertoryService.getOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getProductId, orderDetail.getProductId()).eq(ProductRepertory::getUid, orderFill.getUid()));
            if (productRepertory.getCount() - orderDetail.getPayNum() < 0) {
                ifFill = true;
            }
        }
        //库存不足，无法补单
        if (ifFill) {
            return;
        }

        for (OrderDetail orderDetail : orderDetailList) {
            productRepertoryService.reduce(orderDetail.getProductId(), orderDetail.getPayNum(), orderFill.getUid(), orderFill.getOrderNo() + "补单", orderFill.getOrderNo(), "补单");
        }
        orderFill.setStatus(OrderFillType.已补单.getName());
        orderFill.setFillTime(new Date());
        dao.updateById(orderFill);
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

