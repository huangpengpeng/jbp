package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 间推佣金处理器
 */
@Component
public class IndirectCommHandler extends AbstractProductCommHandler{

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

    @Override
    public Integer getType() {
        return ProductCommEnum.间推佣金.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.间推佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rule = getRule(productComm);
        Set<Integer> set = rule.stream().map(Rule::getCapaId).collect(Collectors.toSet());
        if (set.size() != rule.size()) {
            throw new CrmebException("等级配置不能重复");
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
    public List<Rule> getRule(ProductComm productComm) {
        try {
            return JSONArray.parseArray(productComm.getRule(), Rule.class);
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
        // 没有上级直接返回
        Integer pid = invitationService.getPid(order.getUid());
        if (pid == null) {
            return;
        }
        pid = invitationService.getPid(pid);
        if (pid == null) {
            return;
        }
        BigDecimal totalAmt = BigDecimal.ZERO;
        // 上级等级
        UserCapa pUserCapa = userCapaService.getByUser(pid);
        Long capaId = pUserCapa == null ? capaService.getMinCapa().getId() : pUserCapa.getCapaId();
        // 获取订单产品
        List<FundClearingProduct> productList = Lists.newArrayList();
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()); // 商品总价
            // 佣金不存在或者关闭直接忽略
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 钱包抵扣PV
            BigDecimal totalPv = payPrice.add(getWalletDeductionListPv(orderDetail));
            totalPv = totalPv.multiply(productComm.getScale());
            // 获取佣金规则
            List<Rule> rules = getRule(productComm);
            Map<Integer, Rule> ruleMap = FunctionUtil.keyValueMap(rules, Rule::getCapaId);
            Rule rule = ruleMap.get(capaId);
            BigDecimal ratio = BigDecimal.ZERO;
            if (rule != null) {
                ratio = rule.getRatio();
            }
            // 计算订单金额
            BigDecimal amt = ratio.multiply(totalPv);
            totalAmt = totalAmt.add(amt);
            FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                    orderDetail.getPayNum(), ratio, amt);
            productList.add(clearingProduct);
        }
        // 按订单保存佣金
        totalAmt = totalAmt.setScale(2, BigDecimal.ROUND_DOWN);
        if (ArithmeticUtils.gt(totalAmt, BigDecimal.ZERO)) {
            User orderUser = userService.getById(order.getUid());
            fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.间推佣金.getName(), totalAmt,
                    null, productList, orderUser.getAccount() + "下单获得" + ProductCommEnum.间推佣金.getName(), "");
        }
    }

    /**
     * 间接佣金规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 等级
         */
        private Integer capaId;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}