package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.FundClearingRecord;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingRecordRequest;
import com.jbp.common.request.agent.FundClearingRecordTotalRequest;
import com.jbp.common.response.FundClearingRecordResponse;
import com.jbp.common.response.FundClearingRecordSelfResponse;

import java.math.BigDecimal;
import java.util.List;

public interface FundClearingRecordService extends IService<FundClearingRecord> {

    FundClearingRecord add(User user, User orderUser, String externalNo, String commName, BigDecimal commAmt,
                           Integer productId, String productName, BigDecimal price, BigDecimal score, Integer quantity,
                           BigDecimal rewardValue, String rewardType, String description);


    FundClearingRecord create(User user, User orderUser, OrderDetail orderDetail, String commName, BigDecimal commAmt, BigDecimal score, BigDecimal rewardValue, String rewardType, String description);

    boolean refund(String externalNo);

    PageInfo<FundClearingRecord> pageList(FundClearingRecordRequest request, PageParamRequest pageParamRequest);

    void updateCancel(List<Long> ids);

    PageInfo<FundClearingRecordResponse> total(FundClearingRecordTotalRequest request, PageParamRequest pageParamRequest);

    FundClearingRecordSelfResponse selfTotal(Integer uid);

    PageInfo<FundClearingRecord> detail(Integer uid,String day, PageParamRequest pageParamRequest);

}
