package com.jbp.service.product.comm;


import com.alibaba.fastjson.JSONObject;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * 店铺佣金
 */
@Component
public class ShopCommHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private FundClearingService fundClearingService;


    @Override
    public Integer getType() {
        return ProductCommEnum.店铺佣金.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        return null;
    }

    @Override
    public Rule getRule(ProductComm productComm) {
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            return JSONObject.parseObject(productCommConfig.getRatioJson(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList) {
        ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
        if (!productCommConfig.getStatus()) {
            return;
        }
        Rule rule = getRule(null);
        if (rule == null || !ArithmeticUtils.gt(rule.getRatio(), BigDecimal.ZERO)) {
            return;
        }
        // 增加对碰积分业绩
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        // 订单总PV
        BigDecimal score = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 佣金不存在或者关闭直接忽略
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()); // 商品总价
            // 总PV
            BigDecimal totalPv = payPrice.add(getWalletDeductionListPv(orderDetail));// 钱包抵扣PV
            totalPv = BigDecimal.valueOf(totalPv.multiply(productComm.getScale()).intValue());
            // 订单总PV
            score = score.add(totalPv);
        }

        BigDecimal ratio = rule.getRatio();
        // 店铺佣金
        BigDecimal amt = score.multiply(ratio).setScale(2, BigDecimal.ROUND_DOWN);
        if (!ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
            return;
        }
        // 下单用户信息
        User orderUser = userService.getById(order.getUid());
        Integer uid = order.getUid();
        do {
            Integer pid = invitationService.getPid(uid);
            if (pid == null) {
                break;
            }
            final Boolean openShop = userService.getById(pid).getOpenShop();
            if (openShop != null && BooleanUtils.isTrue(openShop)) {
                fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.店铺佣金.getName(), amt,
                        null, null, orderUser.getAccount() + "下单, 奖励" + ProductCommEnum.店铺佣金.getName(), "");
                break;
            }
            uid = pid;
        } while (true);

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}
