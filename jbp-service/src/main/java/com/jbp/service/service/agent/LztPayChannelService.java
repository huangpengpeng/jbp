package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LztPayChannel;

import java.util.List;

public interface LztPayChannelService extends IService<LztPayChannel> {

    LztPayChannel add(LztPayChannel lztPayChannel);

   List<LztPayChannel> getByMer(Integer merId);

    LztPayChannel getByMer(Integer merId, String type);


}
