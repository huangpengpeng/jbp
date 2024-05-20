package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.InvitationScore;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ScoreDownloadRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface InvitationScoreService extends IService<InvitationScore> {

    void init();

    PageInfo<InvitationScore> pageList(Integer uid, PageParamRequest pageParamRequest);

    InvitationScore add(Integer uid);

    InvitationScore getByUser(Integer uid);

    /**
     * 团队业绩
     */
    BigDecimal getInvitationScore(Integer uid, Boolean containsSelf);

    void orderSuccess(Integer uid, BigDecimal score, String ordersSn, Date payTime, List<ProductInfoDto> productInfo);

    void orderRefund(String orderSn);

    String download(ScoreDownloadRequest request);
}
