package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.jbp.common.constants.OrderStatusConstants;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.OrderSuccessMsg;
import com.jbp.common.model.agent.ProductMaterials;
import com.jbp.common.model.order.Materials;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.dao.agent.OrderSuccessMsgDao;
import com.jbp.service.product.comm.CommCalculateResult;
import com.jbp.service.product.comm.ProductCommChain;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.OrderStatusService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrderSuccessMsgServiceImpl extends ServiceImpl<OrderSuccessMsgDao, OrderSuccessMsg> implements OrderSuccessMsgService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ProductCommChain productCommChain;
    @Autowired
    private SelfScoreService selfScoreService;
    @Autowired
    private InvitationScoreService invitationScoreService;
    @Autowired
    private OrdersFundSummaryService ordersFundSummaryService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private UserCapaXsService userCapaXsService;
    @Autowired
    private ProductMaterialsService productMaterialsService;

    @Override
    public void add(String orderSn) {
        OrderSuccessMsg msg = new OrderSuccessMsg();
        msg.setOrdersSn(orderSn);
        msg.setExec(false);
        save(msg);
    }

    @Override
    public void exec(OrderSuccessMsg msg) {
        String orderNo = msg.getOrdersSn();
        Order platOrder = orderService.getByOrderNo(msg.getOrdersSn());
        if (ObjectUtil.isNull(platOrder)) {
            throw new RuntimeException("订单不存在，orderNo: " + platOrder.getOrderNo());
        }
        if (ordersFundSummaryService.getByOrdersSn(platOrder.getOrderNo()) != null) {
            throw new RuntimeException("订单已经执行重复：" + platOrder.getOrderNo());
        }
        // 2.增加业绩
        BigDecimal score = BigDecimal.ZERO;
        List<ProductInfoDto> productInfoList = Lists.newArrayList();
        List<OrderDetail> platOrderDetailList = orderDetailService.getByOrderNo(platOrder.getOrderNo());
        for (OrderDetail orderDetail : platOrderDetailList) {
            BigDecimal realScore = orderDetailService.getRealScore(orderDetail);
            score = score.add(realScore);
            ProductInfoDto productInfo = new ProductInfoDto(orderDetail.getProductId(), orderDetail.getProductName(),
                    orderDetail.getPayNum(), orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()), realScore);
            productInfoList.add(productInfo);
        }

        // 获取拆单后订单
        List<Order> orderList = orderService.getByPlatOrderNo(platOrder.getOrderNo());
        if (CollUtil.isEmpty(orderList)) {
            throw new RuntimeException("商户订单信息不存在，orderNo: " + orderNo);
        }

        List<OrderDetail> orderDetailList = CollUtil.newArrayList();
        for (Order order : orderList) {
            // 拆单后，一个主订单只会对应一个商户订单
            List<OrderDetail> merOrderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            // 处理ERP商品转换
            for (OrderDetail orderDetail : merOrderDetailList) {
                BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee());
                List<Materials> materialsList = Lists.newArrayList();
                if (StringUtils.isNotEmpty(orderDetail.getBarCode())) {
                    List<ProductMaterials> productMaterialsList = productMaterialsService.getByBarCode(orderDetail.getMerId(), orderDetail.getBarCode());
                    BigDecimal totalPrice = BigDecimal.ZERO;
                    for (ProductMaterials productMaterials : productMaterialsList) {
                        totalPrice = totalPrice.add(productMaterials.getMaterialsPrice().multiply(BigDecimal.valueOf(productMaterials.getMaterialsQuantity())));
                    }
                    for (ProductMaterials productMaterials : productMaterialsList) {
                        BigDecimal price = productMaterials.getMaterialsPrice().multiply(BigDecimal.valueOf(productMaterials.getMaterialsQuantity()));
                        Materials materials = new Materials(productMaterials.getMaterialsName(),
                                productMaterials.getMaterialsQuantity() * orderDetail.getPayNum(),
                                productMaterials.getMaterialsPrice(), productMaterials.getMaterialsCode(),
                                ArithmeticUtils.equals(BigDecimal.ZERO, totalPrice) ? BigDecimal.ZERO : payPrice.multiply(price.divide(totalPrice, 4, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN));
                        materialsList.add(materials);
                    }
                    orderDetail.setMaterialsList(materialsList);
                }
            }
            orderDetailList.addAll(merOrderDetailList);
        }
        boolean b = true;
        // 1..资金概况
        ordersFundSummaryService.create(platOrder.getId(), platOrder.getOrderNo(),
                platOrder.getPayPrice().subtract(platOrder.getPayPostage()), score);
        // 2.自有业绩
        selfScoreService.orderSuccess(platOrder.getUid(), score, orderNo, platOrder.getPayTime(), productInfoList);
        // 3.团队业绩
        invitationScoreService.orderSuccess(platOrder.getUid(), score, orderNo, platOrder.getPayTime(), productInfoList);
        // 4.个人升级
        userCapaService.riseCapa(platOrder.getUid());
        userCapaXsService.riseCapaXs(platOrder.getUid());
        // 5.分销佣金
        LinkedList<CommCalculateResult> commList = new LinkedList<>();
        productCommChain.orderSuccessCalculateAmt(platOrder, commList);
        // 订单信息
        b = orderDetailService.updateBatchById(orderDetailList);
        if (!b) {
            throw new RuntimeException("执行订单成功消息失败[更新订单详情]:" + msg.getOrdersSn());
        }
        // 订单日志
        orderList.forEach(o -> orderStatusService.createLog(o.getOrderNo(), OrderStatusConstants.ORDER_STATUS_PAY_SPLIT, StrUtil.format(OrderStatusConstants.ORDER_LOG_MESSAGE_PAY_SPLIT, platOrder.getOrderNo())));
        msg.setExec(true);
        b = updateById(msg);
        if (!b) {
            throw new RuntimeException("执行订单成功消息失败:" + msg.getOrdersSn());
        }
    }
}