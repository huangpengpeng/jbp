package com.jbp.service.product.comm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
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
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.店铺佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rule = getRule(productComm);
        if(rule == null || rule.getRatio() == null){
            throw new CrmebException(ProductCommEnum.店铺佣金.getName() + "参数不完整");
        }
        // 删除数据库的信息
        productCommService.remove(new LambdaQueryWrapper<ProductComm>()
                .eq(ProductComm::getProductId, productComm.getProductId())
                .eq(ProductComm::getType, productComm.getType()));
        // 保存最新的信息
        productCommService.save(productComm);
        return true;
    }

    @Override
    public Rule getRule(ProductComm productComm) {
        try {
            return JSONObject.parseObject(productComm.getRule(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {

        List<FundClearingProduct> productList = Lists.newArrayList();

        // 订单总PV
        BigDecimal score = BigDecimal.ZERO;
        BigDecimal amt = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 佣金不存在或者关闭直接忽略
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            Rule rule = getRule(productComm);
            // 总PV
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            totalPv = BigDecimal.valueOf(totalPv.multiply(productComm.getScale()).intValue());
            score = score.add(totalPv);
            BigDecimal productAmt = BigDecimal.ZERO;
            if(StringUtils.isNotEmpty(rule.getType()) && StringUtils.equals(rule.getType(), "金额")){
                productAmt = rule.getRatio().multiply(BigDecimal.valueOf(orderDetail.getPayNum())).setScale(2, BigDecimal.ROUND_DOWN);
            }else{
                productAmt = totalPv.multiply(rule.getRatio()).setScale(2, BigDecimal.ROUND_DOWN);
            }
            FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                    orderDetail.getPayNum(), rule.getRatio(), productAmt);
            productList.add(clearingProduct);
            // 店铺佣金
            amt = amt.add(productAmt);
        }
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
                         productList, orderUser.getAccount() + "下单, 奖励" + ProductCommEnum.店铺佣金.getName(), "");

                int sort = resultList.size() + 1;
                CommCalculateResult calculateResult = new CommCalculateResult(pid, getType(), ProductCommEnum.店铺佣金.getName(),
                        null, null, BigDecimal.ZERO,
                        1, score, BigDecimal.ONE, BigDecimal.ZERO, amt, sort);
                resultList.add(calculateResult);
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


        /**
         * 比例 类型  金额  比例
         */
        private String type;
    }
}
