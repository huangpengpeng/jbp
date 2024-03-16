package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.vo.InvitationScoreFlowVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface InvitationScoreFlowService extends IService<InvitationScoreFlow> {
     PageInfo<InvitationScoreFlow> pageList(Integer uid, Integer orderuid, String action, String ordersSn,String dateLimit, PageParamRequest pageParamRequest);

     InvitationScoreFlow add(Integer uid, Integer orderUid, BigDecimal score, String action,
                             String operate, String ordersSn, Date payTime, List<ProductInfoDto> productInfo, String remark);

     List<InvitationScoreFlowVo> excel(Integer uid, Integer orderuid, String action, String ordersSn, String dateLimit);
}
