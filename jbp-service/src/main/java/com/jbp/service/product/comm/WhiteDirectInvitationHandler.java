package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WhiteUserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 直接推荐佣金
 */
@Component
public class WhiteDirectInvitationHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private CapaService capaService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private UserService userService;
    @Resource
    private WhiteUserService whiteUserService;

    @Override
    public Integer getType() {
        return ProductCommEnum.白名单直推佣金.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.白名单直推佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        Rule rules = getRule(productComm);
        if (rules == null) {
            throw new CrmebException(ProductCommEnum.白名单直推佣金.getName() + "参数不完整");
        }

        if (rules == null || rules.getWhiteId() == null) {
            throw new CrmebException(ProductCommEnum.白名单直推佣金.getName() + "参数不完整");
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
            ProductCommConfig config = productCommConfigService.getByType(getType());
            WhiteDirectInvitationHandler.Rule rules = JSONObject.parseObject(config.getRatioJson(), WhiteDirectInvitationHandler.Rule.class);
            return rules;
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {

        // 没有上级直接返回
        List<UserUpperDto> allUpper = invitationService.getNoMountAllUpper(order.getUid());
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        Integer pid = 0;
        BigDecimal totalAmt = BigDecimal.ZERO;
        // 获取订单产品
        List<FundClearingProduct> productList = Lists.newArrayList();

        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            // 佣金不存在或者关闭直接忽略
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 钱包抵扣PV
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            totalPv = totalPv.multiply(productComm.getScale());
            // 获取佣金规则
            Rule rules = getRule(productComm);

            //获取白名单内的上级
            for (UserUpperDto upperDto : allUpper) {
                if (upperDto.getPId() != null) {
                    WhiteUser whiteUser = whiteUserService.getByUser(upperDto.getPId(), rules.getWhiteId());
                    if (whiteUser != null) {
                        pid = upperDto.getPId();
                        break;
                    }
                }
            }

            if (pid == 0) {
                break;
            }

            UserCapa pUserCapa = userCapaService.getByUser(pid);
            Long capaId = pUserCapa == null ? capaService.getMinCapa().getId() : pUserCapa.getCapaId();
            List<CapaRule> capaRuleList = rules.getCapaRuleList();
            Map<Long, CapaRule> ruleMap = FunctionUtil.keyValueMap(capaRuleList, CapaRule::getCapaId);
            CapaRule capaRule = ruleMap.get(capaId);

            BigDecimal ratio = BigDecimal.ZERO;
            if (capaRule != null) {
                ratio = capaRule.getRatio();
            }
            // 计算订单金额
            BigDecimal amt = BigDecimal.ZERO;
            if (capaRule != null && StringUtils.isNotEmpty(capaRule.getType()) && StringUtils.equals(capaRule.getType(), "金额")) {
                amt = capaRule.getRatio().multiply(BigDecimal.valueOf(orderDetail.getPayNum())).setScale(2, BigDecimal.ROUND_DOWN);
            } else {
                amt = ratio.multiply(totalPv);
            }
            totalAmt = totalAmt.add(amt);
            FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                    orderDetail.getPayNum(), ratio, amt);
            productList.add(clearingProduct);
        }

        // 按订单保存佣金
        totalAmt = totalAmt.setScale(2, BigDecimal.ROUND_DOWN);
        if (ArithmeticUtils.gt(totalAmt, BigDecimal.ZERO)) {
            User orderUser = userService.getById(order.getUid());
            fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.直推佣金.getName(), totalAmt,
                    productList, orderUser.getNickname() + "|" + orderUser.getAccount() + "下单获得" + ProductCommEnum.直推佣金.getName(), "");
        }
    }

    /**
     * 直推佣金规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CapaRule {
        /**
         * 等级
         */
        private Long capaId;

        /**
         * ou
         * 比例
         */
        private BigDecimal ratio;

        /**
         * 比例 类型  金额  比例
         */
        private String type;


    }


    /**
     * 直推佣金规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        private List<CapaRule> capaRuleList;
        /**
         * 白名单id
         */
        private Long whiteId;
    }


}
