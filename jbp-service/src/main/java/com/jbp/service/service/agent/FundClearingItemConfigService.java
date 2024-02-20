package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.FundClearingItemConfig;
import com.jbp.common.request.agent.FundClearingItemConfigRequest;

import java.math.BigDecimal;
import java.util.List;

public interface FundClearingItemConfigService extends IService<FundClearingItemConfig> {
    void save (List<FundClearingItemConfigRequest> list);
}
