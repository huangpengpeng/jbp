package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.SelfScore;
import com.jbp.common.request.PageParamRequest;

public interface SelfScoreService extends IService<SelfScore> {
  PageInfo<SelfScore> pageList(Integer uid, PageParamRequest pageParamRequest);


}
