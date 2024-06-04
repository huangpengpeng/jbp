package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface LztPayChannelService extends IService<LztPayChannel> {

    PageInfo<LztPayChannel> pageList(Integer merId, PageParamRequest pageParamRequest);

    LztPayChannel add(LztPayChannel lztPayChannel);

   List<LztPayChannel> getByMer(Integer merId);

    LztPayChannel getByMer(Integer merId, String type);


}
