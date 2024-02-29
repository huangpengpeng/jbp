package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.RelationScore;
import com.jbp.common.model.agent.RelationScoreFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.RelationScoreResponse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RelationScoreService extends IService<RelationScore> {
    RelationScore getByUser(Integer uId, Integer node);

    PageInfo<RelationScore> pageList(Integer uid, PageParamRequest pageParamRequest);

    RelationScoreFlow orderSuccessIncrease(Integer uid, Integer orderUid, BigDecimal score, int node, String ordersSn,
                                    Date payTime, List<ProductInfoDto> productInfo, Integer level);

    RelationScoreFlow orderSuccessReduce(Integer uid, Integer orderUid, BigDecimal score, int node,
                                         String ordersSn, Date payTime, Integer level, BigDecimal amt, BigDecimal ratio);

    void operateIncreaseUsable(Integer uid, int score, int node, String ordersSn, Date payTime, String remark);



    void operateReduceUsable(Integer uid, BigDecimal score, int node, String ordersSn,
                         Date payTime, String remark, Boolean ifUpdateUsed);


    RelationScoreResponse getUserResult();

}
