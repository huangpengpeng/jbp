package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Lottery;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.LotteryRequest;
import com.jbp.common.request.agent.LotterySearchRequest;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public interface LotteryService extends IService<Lottery> {
    boolean add(LotteryRequest request);

    boolean edit(LotteryRequest request);

    PageInfo<Lottery> pageList(LotterySearchRequest request, PageParamRequest pageParamRequest);
}
