package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.FundClearingItemConfig;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.FundClearingItemConfigRequest;

import java.util.List;

public interface FundClearingItemConfigService extends IService<FundClearingItemConfig> {
    void save(List<FundClearingItemConfigRequest> list);

    PageInfo<FundClearingItemConfig> pageList(String commName, PageParamRequest pageParamRequest);

}
