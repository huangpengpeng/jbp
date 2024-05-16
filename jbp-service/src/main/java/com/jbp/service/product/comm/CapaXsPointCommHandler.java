package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 星级见点佣金
 */
@Component
public class CapaXsPointCommHandler extends AbstractProductCommHandler {

    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ProductCommConfigService productCommConfigService;

    @Override
    public Integer getType() {
        return ProductCommEnum.星级见点佣金.getType();
    }

    @Override
    public Integer order() {
        return 2;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.星级见点佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rule = getRule(productComm);
        if (rule.getCapaXsId() == null || rule.getXsComm() == null) {
            throw new CrmebException(ProductCommEnum.星级见点佣金.getName() + "参数不完整");
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
            CapaXsPointCommHandler.Rule rules = JSONObject.parseObject(productComm.getRule(), CapaXsPointCommHandler.Rule.class);
            return rules;
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {

        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            // 佣金配置
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 真实业绩
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            totalPv = totalPv.multiply(productComm.getScale());
            // 佣金规则
            Rule rule = getRule(productComm);
            int i = 0;
            Integer pid = invitationService.getPid(order.getUid());

            do {
                if (pid == null) {
                    break;
                }
                UserCapaXs userCapaXs = userCapaXsService.getByUser(pid);

                //判断星级是否满足佣金规则
                if (userCapaXs == null || userCapaXs.getCapaId().intValue() < rule.getCapaXsId().intValue()) {
                    pid = invitationService.getPid(pid);
                    continue;
                }

                CapaXsPointCommHandler.Comm comm = rule.getXsComm().get(i);
                BigDecimal amount = totalPv.multiply(comm.getRatio());
                List<FundClearingProduct> fundClearingProducts = new ArrayList<>();
                FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                        orderDetail.getPayNum(), comm.getRatio(), amount);
                fundClearingProducts.add(clearingProduct);
                User orderUser = userService.getById(order.getUid());
                fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.星级见点佣金.getName(), amount,
                        fundClearingProducts, orderUser.getAccount() + "下单获得" + ProductCommEnum.星级见点佣金.getName(), "");
                pid = invitationService.getPid(pid);
                i++;
            } while (i < rule.getXsComm().size());


        }

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        private Long capaXsId;
        private List<Comm> xsComm;
    }

    /**
     * 等级比例
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comm {
        /**
         * 代数
         */
        private int num;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}