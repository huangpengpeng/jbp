package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.RelationScoreResponse;
import com.jbp.common.vo.RelationScoreVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RelationScoreService extends IService<RelationScore> {
    RelationScore getByUser(Integer uId, Integer node);

    PageInfo<RelationScore> pageList(Integer uid, PageParamRequest pageParamRequest);

    RelationScoreFlow orderSuccessIncrease(Integer uid, Integer orderUid, BigDecimal score, int node, String ordersSn,
                                           Date payTime, List<ProductInfoDto> productInfo, Integer level);

    RelationScoreFlow orderSuccessReduce(Integer uid, Integer orderUid, BigDecimal score, int node,
                                         String ordersSn, Date payTime, Integer level, BigDecimal amt, BigDecimal ratio, String remak);

    void operateUsable(Integer uid, BigDecimal score, int node, String ordersSn, Date payTime, String remark, Boolean ifAdd);

    void operateUsed(Integer uid, BigDecimal score, int node, String ordersSn, Date payTime, String remark, Boolean ifAdd);


    RelationScoreResponse getUserResult();

    List<RelationScoreVo> excel(Integer uid);
}
