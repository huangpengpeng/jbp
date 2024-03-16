package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.RelationScoreFlowVo;

import java.util.List;

public interface RelationScoreFlowService extends IService<RelationScoreFlow> {

     PageInfo<RelationScoreFlow> pageList(Integer uid, Integer orderuid,String ordersSn,String dateLimit, PageParamRequest pageParamRequest);

    List<RelationScoreFlowVo> excel(Integer uid, Integer orderuid, String ordersSn, String dateLimit);
}
