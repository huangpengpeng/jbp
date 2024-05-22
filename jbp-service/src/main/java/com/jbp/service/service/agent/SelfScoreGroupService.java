package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.model.agent.SelfScoreGroup;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface SelfScoreGroupService extends IService<SelfScoreGroup> {
    PageInfo<SelfScoreGroup> pageList(Integer uid,String groupName, String action, String nickname,PageParamRequest pageParamRequest);

}
