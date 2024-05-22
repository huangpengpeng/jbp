package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScoreGroup;
import com.jbp.common.request.PageParamRequest;

public interface RelationScoreGroupService extends IService<RelationScoreGroup> {
    PageInfo<RelationScoreGroup> pageList(Integer uid, String groupName, String nickname,PageParamRequest pageParamRequest);
}
