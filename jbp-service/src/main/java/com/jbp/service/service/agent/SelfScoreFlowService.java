package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.SelfScoreFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.SelfScoreFlowVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface SelfScoreFlowService extends IService<SelfScoreFlow> {
    PageInfo<SelfScoreFlow> pageList(Integer uid, String action,String ordersSn,String dateLimit, PageParamRequest pageParamRequest);

    SelfScoreFlow add(Integer uid, BigDecimal score, String action, String operate,
                      String ordersSn, Date payTime, List<ProductInfoDto> productInfo, String remark);

    List<SelfScoreFlowVo> excel(Integer uid, String action, String ordersSn, String dateLimit);

}
