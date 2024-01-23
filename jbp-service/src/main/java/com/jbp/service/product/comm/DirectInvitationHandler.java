package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.ProductCommService;

import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 直接推荐佣金
 */
@Component
public class DirectInvitationHandler implements AbstractProductCommHandler {

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

    @Override
    public Integer getType() {
        return ProductCommEnum.直推佣金.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.直推佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rule = getRule(productComm);
        Set<Integer> set = rule.stream().map(Rule::getCapaId).collect(Collectors.toSet());
        if(set.size() != rule.size()){
            throw new CrmebException("等级配置不能重复");
        }
        // 删除数据库的信息
        productCommService.deleteByProduct(productComm.getProductId(), productComm.getType());
        // 保存最新的信息
        productCommService.save(productComm);
        return true;


    }

    @Override
    public List<DirectInvitationHandler.Rule> getRule(ProductComm productComm) {
        try {
            return JSONArray.parseArray(productComm.getRule(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<CommCalculateResult> resultList) {
        // 没有上级直接返回
        Integer pid = invitationService.getPid(order.getUid());
        if (pid == null) {
            return;
        }
        // 上级等级
        UserCapa pUserCapa = userCapaService.getByUser(pid);
        Long capaId = pUserCapa == null ? capaService.getMinCapa().getId() : pUserCapa.getCapaId();
        // 获取订单产品
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 佣金不存在或者关闭直接忽略
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 获取佣金规则
            List<Rule> rules = getRule(productComm);
            Map<Integer, Rule> ruleMap = FunctionUtil.keyValueMap(rules, Rule::getCapaId);
            Rule rule = ruleMap.get(capaId);
            if (rule == null) {
                continue;
            }
            // 获得佣金比例
            BigDecimal ratio = rule.getRatio();
            // 计算订单金额
            BigDecimal amt = BigDecimal.ZERO;


            if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                CommCalculateResult result = new CommCalculateResult(getType(), ProductCommEnum.直推佣金.getName(),
                        productId, orderDetail.getProductName(), orderDetail.getPayPrice(),
                        orderDetail.getPayNum(), BigDecimal.ZERO, productComm.getScale(), ratio, amt);

                resultList.add(result);
            }
        }
    }

    /**
     * 直推佣金规则
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
