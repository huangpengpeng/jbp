package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.request.PageParamRequest;

public interface InvitationScoreService extends IService<InvitationScore> {
    PageInfo<InvitationScore> pageList(Integer uid, PageParamRequest pageParamRequest);

}
