package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.RelationScoreFlowVo;

import java.util.List;

public interface RelationScoreFlowService extends IService<RelationScoreFlow> {

    PageInfo<RelationScoreFlow> pageList(Integer uid, Integer orderUid, String ordersSn, String dateLimit, Integer node, String action, PageParamRequest pageParamRequest);

    List<RelationScoreFlowVo> excel(Integer uid, Integer orderUid, String ordersSn, String dateLimit, Integer node, String action);

    List<RelationScoreFlow> getByOrders(String ordersSn, String operate, String action);

}
