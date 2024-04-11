package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.ClearingUser;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.ClearingFinalService;
import com.jbp.service.service.agent.ClearingUserService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 月结扩展佣金
 */
@Slf4j
@Component
public class MonthExtCommHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ClearingFinalService clearingFinalService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ClearingUserService clearingUserService;


    @Override
    public Integer getType() {
        return ProductCommEnum.拓展佣金.getType();
    }

    @Override
    public Integer order() {
        return 10;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.拓展佣金.getName() + "参数不完整");
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
    public <T> T getRule(ProductComm productComm) {
        return null;
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList) {

    }

    public void clearing(Long clearingId) {
        ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
        if (!productCommConfig.getIfOpen()) {
            throw new CrmebException("当前佣金配置未开启请联系管理员");
        }

        ClearingFinal clearingFinal = clearingFinalService.getById(clearingId);
        if(!clearingFinal.getStatus().equals(ClearingFinal.Constants.待结算.name())){
            throw new CrmebException("佣金状态不是待结算不允许结算");
        }
        Date startTime = DateTimeUtils.parseDate(clearingFinal.getStartTime());
        Date endTime = DateTimeUtils.parseDate(clearingFinal.getEndTime());
        List<Order> successList = orderService.getSuccessList(startTime, endTime);

        BigDecimal totalScore = BigDecimal.ZERO;
        Map<Integer, ProductComm> map = Maps.newConcurrentMap();
        for (Order order : successList) {
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            for (OrderDetail orderDetail : orderDetailList) {
                ProductComm productComm = map.get(orderDetail.getProductId());
                if (productComm == null) {
                    productComm = productCommService.getByProduct(orderDetail.getProductId(), getType());
                    map.put(orderDetail.getProductId(), productComm);
                }
                // 佣金不存在或者关闭直接忽略
                if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                    continue;
                }
                BigDecimal realScore = orderDetailService.getRealScore(orderDetail);
                realScore = BigDecimal.valueOf(realScore.multiply(productComm.getScale()).intValue());
                totalScore = totalScore.add(realScore);
            }
        }

        // 积分为0直接已出款退出
        if(ArithmeticUtils.lessEquals(totalScore, BigDecimal.ZERO)){
            log.error(clearingFinal.getName()+"结算积分为0");
            clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
            clearingFinalService.updateById(clearingFinal);
            return;
        }
        List<ClearingUser> clearingUsers = clearingUserService.getByClearing(clearingId);
        if(CollectionUtils.isEmpty(clearingUsers)){
            throw new CrmebException("请导入结算名单");
        }
        // 权重汇总
        BigDecimal sum = clearingUsers.stream().map(p -> JSONObject.parseObject(p.getRule()).getBigDecimal("weight"))
                .reduce(BigDecimal.ZERO, (b1, b2) -> b1.add(b2));
        if(ArithmeticUtils.lessEquals(sum, BigDecimal.ZERO)){
            log.error(clearingFinal.getName()+"权重为0");
            clearingFinal.setStatus(ClearingFinal.Constants.已出款.name());
            clearingFinalService.updateById(clearingFinal);
        }
        // 计算每一份多少钱
        BigDecimal divide = totalScore.divide(sum, 2, BigDecimal.ROUND_UP);

        BigDecimal total
        // 计算每个人实际金额
        for (ClearingUser p : clearingUsers) {
            BigDecimal weight = JSONObject.parseObject(p.getRule()).getBigDecimal("weight");
            if(ArithmeticUtils.lessEquals(weight, BigDecimal.ZERO)){
                continue;
            }
            BigDecimal commFee = divide.multiply(weight).setScale(2, BigDecimal.ROUND_UP);



        }


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        // 权重
        private BigDecimal weight;
    }

}
