package com.jbp.service.product.comm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.order.Order;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;

/**
 * 月结培育佣金
 */
@Slf4j
@Component
public class MonthPyCommHandler extends AbstractProductCommHandler {

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
    @Resource
    private ClearingBonusService clearingBonusService;
    @Resource
    private ClearingBonusFlowService clearingBonusFlowService;

    @Override
    public Integer getType() {
        return ProductCommEnum.培育佣金.getType();
    }


    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.培育佣金.getName() + "参数不完整");
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
    public void clearing(ClearingFinal clearingFinal) {

    }
}
