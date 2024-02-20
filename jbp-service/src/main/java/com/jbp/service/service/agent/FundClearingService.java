package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.FundClearingItem;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.UserInfo;

import java.math.BigDecimal;
import java.util.List;

public interface FundClearingService extends IService<FundClearing> {

    FundClearing create(Integer uid, String externalNo, String commName, BigDecimal commAmt,
                     List<FundClearingItem> items, List<FundClearingProduct> productList,
                     String description, String remark);

    List<FundClearing> getByUser(Integer uid, String commName, List<String> statusList);


}
