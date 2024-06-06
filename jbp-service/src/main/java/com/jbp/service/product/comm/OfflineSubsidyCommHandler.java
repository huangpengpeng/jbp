package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.MerchantOrder;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.MerchantOrderService;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
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
 * 升级等级级差
 */
@Component
public class OfflineSubsidyCommHandler extends AbstractProductCommHandler {

    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserOfflineSubsidyService userOfflineSubsidyService;
    @Resource
    private MerchantOrderService merchantOrderService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private TeamUserService teamUserService;


    @Override
    public Integer getType() {
        return ProductCommEnum.线下补助.getType();
    }

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.线下补助.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rules = getRule(productComm);

        if (rules.getCapaId() == null || rules.getCityComm() == null || ArithmeticUtils.lessEquals(rules.getCityComm(), BigDecimal.ZERO)) {
            throw new CrmebException(ProductCommEnum.线下补助.getName() + "参数不完整");
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
    public OfflineSubsidyCommHandler.Rule getRule(ProductComm productComm) {
        try {
            OfflineSubsidyCommHandler.Rule rules = JSONObject.parseObject(productComm.getRule(), OfflineSubsidyCommHandler.Rule.class);
            return rules;
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {

        User user = userService.getById(order.getUid());
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
            OfflineSubsidyCommHandler.Rule rule = getRule(productComm);

            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
            TeamUser teamUser = teamUserService.getByUser(order.getUid());

            //市公司补助
            UserOfflineSubsidy userOfflineSubsidy = userOfflineSubsidyService.getByArea(merchantOrder.getProvince(), merchantOrder.getCity(), "", UserRegion.Constants.已开通.toString(),teamUser== null? null: teamUser.getName());
            BigDecimal totalCityAmt = BigDecimal.ZERO;
            if (rule.getMode().equals("比例")) {
                totalCityAmt = totalPv.multiply(rule.getCityComm());
            } else {
                totalCityAmt = rule.getCityComm().multiply(new BigDecimal(orderDetail.getPayNum()));
            }
            if (userOfflineSubsidy != null && totalCityAmt.compareTo(BigDecimal.ZERO) == 1) {
                fundClearingService.create(userOfflineSubsidy.getUid(), order.getOrderNo(), ProductCommEnum.线下补助.getName(), totalCityAmt,
                        null, user.getAccount() + "下单获得" + ProductCommEnum.线下补助.getName(), "");
            }


            //区公司补助
            UserOfflineSubsidy userOfflineSubsidy2 = userOfflineSubsidyService.getByArea(merchantOrder.getProvince(), merchantOrder.getCity(), merchantOrder.getDistrict(), UserRegion.Constants.已开通.toString(),teamUser== null? null: teamUser.getName());
            BigDecimal totalAreaAmt = BigDecimal.ZERO;
            if (rule.getMode().equals("比例")) {
                totalAreaAmt = totalPv.multiply(rule.getAreaComm());
            } else {
                totalAreaAmt = rule.getAreaComm().multiply(new BigDecimal(orderDetail.getPayNum()));
            }
            if (userOfflineSubsidy2 != null && totalAreaAmt.compareTo(BigDecimal.ZERO) == 1) {
                fundClearingService.create(userOfflineSubsidy2.getUid(), order.getOrderNo(), ProductCommEnum.线下补助.getName(), totalAreaAmt,
                        null, user.getAccount() + "下单获得" + ProductCommEnum.线下补助.getName(), "");
            }


        }

    }

    /**
     * 等级比例
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 等级
         */
        private Long capaId;

        /**
         * 模式 比例or金额
         */
        private String mode;

        /**
         * 市公司补贴
         */
        private BigDecimal cityComm;
        /**
         * 区公司补贴
         */
        private BigDecimal areaComm;

    }

}
