package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderPayRefundRecord;
import com.jbp.service.dao.OrderPayRefundRecordDao;
import com.jbp.service.service.OrderPayRefundRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrderPayRefundRecordServiceImpl extends ServiceImpl<OrderPayRefundRecordDao, OrderPayRefundRecord> implements OrderPayRefundRecordService {
}
