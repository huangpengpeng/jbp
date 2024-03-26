package com.jbp.service.product.comm;


import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.OrderConstants;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.order.Order;
import com.jbp.service.service.agent.FundClearingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品佣金处理链
 */

@Slf4j
@Component
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ProductCommChain implements ApplicationContextAware {

    @Resource
    private FundClearingService fundClearingService;


    private LinkedList<AbstractProductCommHandler> handlers = new LinkedList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, AbstractProductCommHandler> beans =
                applicationContext.getBeansOfType(AbstractProductCommHandler.class);
        log.warn("ProductCommChain:{}", JSONObject.toJSONString(beans));
        beans.values().stream().sorted(Comparator.comparing(s -> s.order())).forEach(s -> handlers.add(s));
    }

    /**
     * 保存
     */
    public void saveOrUpdate(ProductComm productComm) {
        for (AbstractProductCommHandler handler : handlers) {
            if (handler.getType() == productComm.getType()) {
                handler.saveOrUpdate(productComm);
            }
        }
    }

    /**
     * 订单成功计算佣金
     */
    public void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList) {
        for (AbstractProductCommHandler handler : handlers) {
            if (!fundClearingService.hasCreate(order.getOrderNo(), ProductCommEnum.getCommName(handler.getType()))) {
                handler.orderSuccessCalculateAmt(order, resultList);
            }
        }
    }

    /**
     * 订单退款拦截佣金
     */
    public void orderRefundIntercept(Order order) {
        String orderNo = StringUtil.isEmpty(order.getPlatOrderNo()) ? order.getOrderNo() : order.getPlatOrderNo();
        List<FundClearing> fundClearingList = fundClearingService.getByExternalNo(orderNo, FundClearing.interceptStatus());
        if(CollectionUtils.isEmpty(fundClearingList)){
            return;
        }
        List<Long> ids = fundClearingList.stream().map(FundClearing::getId).collect(Collectors.toList());
        fundClearingService.updateIntercept(ids, "订单申请退款自动拦截");
    }

    /**
     * 订单退款拦截佣金
     */
    public void orderCancelIntercept(Order order) {
        String orderNo = StringUtil.isEmpty(order.getPlatOrderNo()) ? order.getOrderNo() : order.getPlatOrderNo();
        List<FundClearing> fundClearingList = fundClearingService.getByExternalNo(orderNo, Lists.newArrayList(FundClearing.Constants.已拦截.toString()));
        if(CollectionUtils.isEmpty(fundClearingList)){
            return;
        }
        List<Long> ids = fundClearingList.stream().map(FundClearing::getId).collect(Collectors.toList());
        fundClearingService.updateWaitAudit(ids, "订单申请退款拒绝自动恢复");
    }

}
