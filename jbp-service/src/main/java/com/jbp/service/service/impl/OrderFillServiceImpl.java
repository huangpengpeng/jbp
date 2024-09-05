package com.jbp.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.enums.OrderFillType;
import com.jbp.common.enums.SupplyRuleEnum;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductRef;
import com.jbp.common.model.product.ProductRepertory;
import com.jbp.common.model.user.User;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.*;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
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
    @Resource
    private UserService userService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductService productService;
    @Resource
    private ProductRefService productRefService;

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

        List<Map<String, Object>> mapList = new ArrayList<>();
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(orderFill.getOrderNo());

        Boolean ifFill = false;
        for (OrderDetail orderDetail : orderDetailList) {

            List<ProductRef> refs = productRefService.getList(orderDetail.getProductId());
            if (refs.isEmpty()) {
                ProductRepertory productRepertory = productRepertoryService.getOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getProductId, orderDetail.getProductId()).eq(ProductRepertory::getUid, orderFill.getUid()));
                Product product = productService.getById(orderDetail.getProductId());
                if (product.getSupplyRule().equals(SupplyRuleEnum.公司.getName())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("price", orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum())));
                    mapList.add(map);
                    continue;
                }
                if (productRepertory.getCount() - orderDetail.getPayNum() < 0) {
                    ifFill = true;
                }
            } else {
                for (ProductRef ref : refs) {
                    ProductRepertory productRepertory = productRepertoryService.getOne(new QueryWrapper<ProductRepertory>().lambda().eq(ProductRepertory::getProductId, ref.getProductId()).eq(ProductRepertory::getUid, orderFill.getUid()));
                    Product product = productService.getById(ref.getProductId());
                    if (product.getSupplyRule().equals(SupplyRuleEnum.公司.getName())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("price", ref.getPrice().multiply(new BigDecimal(ref.getCount())).multiply(new BigDecimal(orderDetail.getPayNum())));
                        mapList.add(map);
                        continue;
                    }
                    if (productRepertory.getCount() - (orderDetail.getPayNum() * ref.getCount()) < 0) {
                        ifFill = true;
                    } else {
                        ifFill = false;
                        break;
                    }
                }
            }
            if(!ifFill){
                break;
            }
        }
        //库存不足，无法补单
        if (ifFill) {
            return;
        }

        for (OrderDetail orderDetail : orderDetailList) {

            List<ProductRef> refs = productRefService.getList(orderDetail.getProductId());
            if (refs.isEmpty()) {
                Product product = productService.getById(orderDetail.getProductId());
                if (product.getSupplyRule().equals(SupplyRuleEnum.公司.getName())) {
                    continue;
                }
                productRepertoryService.reduce(orderDetail.getProductId(), orderDetail.getPayNum(), orderFill.getUid(), orderFill.getOrderNo() + "补单", orderFill.getOrderNo(), "补单");

            } else {
                for (ProductRef ref : refs) {
                    Product product = productService.getById(ref.getProductId());
                    if (product.getSupplyRule().equals(SupplyRuleEnum.公司.getName())) {
                        continue;
                    }
                    productRepertoryService.reduce(ref.getProductId(), ref.getCount()*orderDetail.getPayNum(), orderFill.getUid(), orderFill.getOrderNo() + "补单", orderFill.getOrderNo(), "补单");

                }
            }


        }
        orderFill.setStatus(OrderFillType.已补单.getName());
        orderFill.setFillTime(new Date());
        dao.updateById(orderFill);


        //货款给补单人
        List<FundClearing> fundClearingList = fundClearingService.list(new QueryWrapper<FundClearing>().lambda().eq(FundClearing::getExternalNo, orderFill.getOrderNo()));
        BigDecimal clearingFee = BigDecimal.ZERO;
        for (FundClearing fundClearing : fundClearingList) {
            clearingFee = clearingFee.add(fundClearing.getCommAmt());
        }

        Order order = orderService.getByOrderNo(orderFill.getOrderNo());


        //计算公司补货金额
        BigDecimal price = BigDecimal.ZERO;
        for (Map<String, Object> map : mapList) {
            price = price.add(new BigDecimal(map.get("price").toString()));
        }


        fundClearingService.create(orderFill.getUid(), orderFill.getOrderNo(), "货款", order.getPayPrice().subtract(clearingFee).subtract(price),
                null, orderFill.getOrderNo() + "下单获得货款", "");
    }


    @Override
    public Map<String, OrderFill> getOrderNoMapList(List<String> orderNoList, String status) {
        LambdaQueryWrapper<OrderFill> lqw = new LambdaQueryWrapper<>();
        lqw.in(OrderFill::getOrderNo, orderNoList);
        lqw.eq(OrderFill::getStatus, status);
        List<OrderFill> list = list(lqw);
        Map<String, OrderFill> orderFillMap = new HashMap<>();
        if (CollectionUtils.isEmpty(list)) {
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

