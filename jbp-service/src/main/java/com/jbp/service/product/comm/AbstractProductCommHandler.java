package com.jbp.service.product.comm;

import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.ProductDeduction;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractProductCommHandler implements ProductCommHandler {

    @Override
    public BigDecimal getWalletDeductionListPv(OrderDetail orderDetail){
        BigDecimal totalPv = BigDecimal.ZERO;
        List<ProductDeduction> walletDeductionList = orderDetail.getWalletDeductionList();
        if (CollectionUtils.isNotEmpty(walletDeductionList)) {
            for (ProductDeduction deduction : walletDeductionList) {
                BigDecimal pv = deduction.getPvFee() == null ? BigDecimal.ZERO : deduction.getPvFee();
                totalPv = pv.add(totalPv);
            }
        }
        return totalPv;
    }


}
