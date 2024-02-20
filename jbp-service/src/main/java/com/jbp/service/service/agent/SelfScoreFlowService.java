package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface SelfScoreFlowService extends IService<SelfScoreFlow> {
    PageInfo<SelfScoreFlow> pageList(Integer uid, String action, PageParamRequest pageParamRequest);

}
