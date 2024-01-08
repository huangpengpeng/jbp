package com.jbp.service.product.comm;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.agent.ProductCommService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 直接推荐佣金
 */
@Component
public class DirectInvitationHandler implements AbstractProductCommHandler {

    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;

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
        getRule(productComm);
        // 删除数据库的信息
        productCommService.deleteByProduct(productComm.getProductId(), productComm.getType());
        // 保存最新的信息
        productCommService.save(productComm);
        return true;


    }

    @Override
    public DirectInvitationHandler.Rule getRule(ProductComm productComm) {
        return null;
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, List<CommCalculateResult> resultList) {
        // 获取订单产品
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());

        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 不存在佣金规则忽略
            if (productComm == null) {
                continue;
            }
            // 存在佣金规则计算佣金
            BigDecimal amt = BigDecimal.ZERO;
            BigDecimal pv = BigDecimal.ZERO; // pv值  根据商品付款金额占比订单总金额  *  订单总PV
            BigDecimal ratio = BigDecimal.ZERO; //  根据规则综合所有条件获取到的获奖比例
            if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                CommCalculateResult result = new CommCalculateResult(getType(), ProductCommEnum.直推佣金.getName(),
                        productId, orderDetail.getProductName(), orderDetail.getPayPrice(),
                        orderDetail.getPayNum(), pv, productComm.getScale(), ratio, amt);

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
         * 自己等级
         */
        private Integer capaId;

    }
}
