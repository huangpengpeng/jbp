package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.service.dao.agent.FundClearingRecordDao;
import com.jbp.service.service.agent.FundClearingRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class FundClearingRecordServiceImpl extends ServiceImpl<FundClearingRecordDao, FundClearingRecord> implements FundClearingRecordService {

    @Override
    public FundClearingRecord add(User user, User orderUser, String externalNo, String commName, BigDecimal commAmt,
                                  Integer productId, String productName, BigDecimal price, BigDecimal score, Integer quantity,
                                  BigDecimal rewardValue, String rewardType, String description) {
        FundClearingRecord record = new FundClearingRecord(user.getId(), user.getAccount(), user.getNickname(), orderUser.getId(),
                orderUser.getAccount(), orderUser.getNickname(),
                externalNo, commName, commAmt, productId, productName, price, score, quantity, rewardValue, rewardType, description);
        save(record);
        return record;
    }

    @Override
    public FundClearingRecord create(User user, User orderUser, OrderDetail orderDetail, String commName, BigDecimal commAmt, BigDecimal score, BigDecimal rewardValue, String rewardType, String description) {
        return add(user, orderUser, orderDetail.getOrderNo(), commName, commAmt,
                orderDetail.getProductId(), orderDetail.getProductName(), orderDetail.getPayPrice(), score, orderDetail.getPayNum(),
                rewardValue, rewardType, description);
    }

    @Override
    public boolean refund(String externalNo) {
        UpdateWrapper<FundClearingRecord> wrapper = new UpdateWrapper<FundClearingRecord>();
        wrapper.lambda().set(FundClearingRecord::getStatus, FundClearingRecord.Constants.取消.name())
                .eq(FundClearingRecord::getExternalNo, externalNo).eq(FundClearingRecord::getStatus, FundClearingRecord.Constants.正常.name());
        return update(wrapper);
    }
}
