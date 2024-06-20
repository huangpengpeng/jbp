package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;

import java.math.BigDecimal;

public interface FundClearingRecordService extends IService<FundClearingRecord> {

    FundClearingRecord add(User user, User orderUser, String externalNo, String commName, BigDecimal commAmt,
                           Integer productId, String productName, BigDecimal price, BigDecimal score, Integer quantity,
                           BigDecimal rewardValue, String rewardType, String description);


    FundClearingRecord create(User user, User orderUser, OrderDetail orderDetail, String commName, BigDecimal commAmt, BigDecimal score, BigDecimal rewardValue, String rewardType, String description);

    boolean refund(String externalNo);
}
