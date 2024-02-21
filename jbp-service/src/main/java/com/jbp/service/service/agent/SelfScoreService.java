package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.agent.SelfScore;
import com.jbp.common.request.PageParamRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface SelfScoreService extends IService<SelfScore> {
  PageInfo<SelfScore> pageList(Integer uid, PageParamRequest pageParamRequest);

  SelfScore add(Integer uid);
  SelfScore getByUser(Integer uid);

  void orderSuccess(Integer uid, BigDecimal score, String ordersSn, Date payTime, List<ProductInfoDto> productInfo);




}
