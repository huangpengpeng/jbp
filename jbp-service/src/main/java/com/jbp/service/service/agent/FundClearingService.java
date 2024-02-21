package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.FundClearingItem;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.UserInfo;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface FundClearingService extends IService<FundClearing> {

    FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt,
                     List<FundClearingItem> items, List<FundClearingProduct> productList,
                     String description, String remark);

    List<FundClearing> getByUser(Integer uid, String commName, List<String> statusList);

     PageInfo<FundClearing> pageList(String uniqueNo, String externalNo, Date startClearingTime, Date endClearingTime, Date startCreateTime, Date endCreateTime, String status, PageParamRequest pageParamRequest);
}
